#!/usr/bin/env python3
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# =============================================================================
# generate_pygamma_fixtures.py
#
# PURPOSE
# -------
# Generate JSON validation fixtures for the Java port of the gamma
# inter-annotator-agreement measure (dkpro-statistics-agreement). The fixtures
# are produced by running the reference implementation *pygamma-agreement* on
# hand-crafted continua and dumping the intermediate + final results the Java
# side will assert against. The validation strategy has three tiers (see
# PLAN_pygamma.md sections 5 and 6):
#
#   tier1 - exact, deterministic core:
#           * per-pair positional / categorical / combined dissimilarities
#           * best-alignment disorder delta(a*) via the canonical fast=False ILP
#   tier2 - exact via replay:
#           * the actual sampled continua from StatisticalContinuumSampler plus
#             each sample's best-alignment disorder. Additionally a
#             scaled-integer replay variant (coordinates * 10^k, rounded to int,
#             kept < 2^24) with the disorder RE-COMPUTED BY PYGAMMA on the
#             scaled integer continuum. Java replays the scaled variant.
#   tier3 - statistical reference only:
#           * expected disorder / gamma / n_samples from compute_gamma with
#             pygamma's own internal 30 samples. This is NOT expected to match
#             tier2 (independent PRNG stream); it is a statistical sanity point.
#
# PROVENANCE
# ----------
# Reference implementation exercised through its PUBLIC API only (this script
# copies no source logic from pygamma; it merely calls Continuum,
# CombinedCategoricalDissimilarity, StatisticalContinuumSampler, etc.):
#
#   package : pygamma-agreement == 0.5.9  (installed from PyPI)
#   repo    : https://github.com/bootphon/pygamma-agreement
#   commit  : 44587ef  (2024-03-04, version 0.5.9) -- analyzed reference
#   license : MIT
#   authors : Rachid Riad, Hadrien Titeux, Leopold Favre  (CoML, 2020-2021)
#
# Because only the public API is used, the pygamma MIT copyright/permission
# notice does not need to be reproduced here. If this script is ever modified to
# copy algorithmic logic out of the pygamma sources, the original MIT copyright
# and permission notice from the corresponding source file MUST be reproduced
# verbatim in this header.
#
# ENVIRONMENT SETUP
# -----------------
# numba (a pygamma dependency) constrains the usable Python version. This
# fixture set was generated with:
#
#   python  : 3.11 (CPython)   -- use an interpreter numba 0.66 supports
#   pygamma : pygamma-agreement==0.5.9
#   solver  : GLPK_MI (cvxpy) -- pygamma tries CBC (needs cylp) first and falls
#             back to GLPK_MI; CBC/cylp is NOT installed here so GLPK_MI is used.
#             GLPK_MI is an exact MIP solver, so the optimal disorder VALUE is
#             identical to what CBC would return (only the value matters).
#
#   python3.11 -m venv pygamma-venv
#   ./pygamma-venv/bin/pip install "pygamma-agreement==0.5.9"
#   ./pygamma-venv/bin/python generate_pygamma_fixtures.py
#
# OUTPUT
# ------
# Pretty-printed JSON files, stable key order, written to
#   dkpro-statistics-agreement/src/test/resources/pygamma/fixture_<nn>_<slug>.json
#
# UNIT ORDERING (documented so Java can reproduce indexes)
# --------------------------------------------------------
# pygamma stores each annotator's units in a SortedSet. Units sort by
# (segment, annotation): first by segment start, then segment end, then
# annotation alphabetically (a None annotation sorts first). Annotators are
# processed in alphabetical order; categories are indexed alphabetically.
# The unitIndexA / unitIndexB values below are positions in that sorted order.
#
# NOTE ON PRECISION
# -----------------
# pygamma computes in np.float32 essentially everywhere. The best-alignment
# disorder comes from the njit float32 d_mat path. The tier1 per-pair
# dissimilarities below are taken from the dissimilarity objects' .d() methods;
# although those methods do their arithmetic in Python float, the final multiply
# by delta_empty (stored as np.float32) rounds each returned value to float32
# too -- e.g. d_pos of 1/9 comes out as 0.11111111 (float32), not the float64
# 0.1111111111111111. So BOTH tier1 pair values and the disorders are float32.
# Java will use double; assert with a relative tolerance of ~1e-5.
# =============================================================================

import json
import logging
import os
from collections import OrderedDict

import numpy as np

# Silence the (expected, repeated) "CBC solver not installed. Using GLPK." warnings.
logging.disable(logging.WARNING)

from pyannote.core import Segment  # noqa: E402
import pygamma_agreement  # noqa: E402
from pygamma_agreement import (  # noqa: E402
    Continuum,
    CombinedCategoricalDissimilarity,
    StatisticalContinuumSampler,
)

# --- provenance constants (also embedded in every fixture's metadata) --------
PYGAMMA_VERSION = getattr(pygamma_agreement, "__version__", "0.5.9")
PYGAMMA_REPO = "https://github.com/bootphon/pygamma-agreement"
PYGAMMA_COMMIT = "44587ef"


def detect_solver():
    """Report which MIP solver pygamma's get_best_alignment will actually use."""
    try:
        import cylp  # noqa: F401
        return "CBC"
    except ImportError:
        return "GLPK_MI"


SOLVER = detect_solver()

# float32 represents integers exactly only up to 2^24; keep scaled coords below.
MAX_EXACT_INT = 1 << 24  # 16777216
SCALE_MARGIN = 16_000_000  # a bit under 2^24 for safety
TIER2_SAMPLES = 10

HERE = os.path.dirname(os.path.abspath(__file__))
OUT_DIR = os.path.abspath(
    os.path.join(HERE, "..", "resources", "pygamma")
)


# -----------------------------------------------------------------------------
# Continuum construction / inspection helpers
# -----------------------------------------------------------------------------
def build_continuum(units):
    """units: iterable of (annotator, start, end, category)."""
    c = Continuum()
    for annotator, start, end, category in units:
        c.add(annotator, Segment(float(start), float(end)), category)
    return c


def continuum_to_units(continuum):
    """Dump the continuum as a flat ordered list of unit dicts.

    Annotators alphabetical; units in each annotator's SortedSet order.
    """
    out = []
    for annotator in continuum.annotators:  # SortedSet -> alphabetical
        for unit in continuum._annotations[annotator]:  # SortedSet order
            out.append(
                OrderedDict(
                    [
                        ("annotator", annotator),
                        ("start", _num(unit.segment.start)),
                        ("end", _num(unit.segment.end)),
                        ("category", unit.annotation),
                    ]
                )
            )
    return out


def _num(x):
    """Emit ints as ints (clean JSON) when the value is integral, else float."""
    xf = float(x)
    if xf.is_integer():
        return int(xf)
    return xf


def sorted_units_by_annotator(continuum):
    """OrderedDict annotator -> list of Unit, in pygamma's sorted order."""
    d = OrderedDict()
    for annotator in continuum.annotators:
        d[annotator] = list(continuum._annotations[annotator])
    return d


# -----------------------------------------------------------------------------
# Tier 1
# -----------------------------------------------------------------------------
def tier1_pair_dissimilarities(continuum, dissim):
    """Every inter-annotator unit pair with positional/categorical/combined d()."""
    by_ann = sorted_units_by_annotator(continuum)
    annotators = list(by_ann.keys())
    pairs = []
    for ia in range(len(annotators)):
        for ib in range(ia + 1, len(annotators)):
            ann_a, ann_b = annotators[ia], annotators[ib]
            for idx_a, unit_a in enumerate(by_ann[ann_a]):
                for idx_b, unit_b in enumerate(by_ann[ann_b]):
                    positional = float(dissim.positional_dissim.d(unit_a, unit_b))
                    categorical = float(dissim.categorical_dissim.d(unit_a, unit_b))
                    combined = float(dissim.d(unit_a, unit_b))
                    pairs.append(
                        OrderedDict(
                            [
                                ("annotatorA", ann_a),
                                ("unitIndexA", idx_a),
                                ("annotatorB", ann_b),
                                ("unitIndexB", idx_b),
                                ("positional", positional),
                                ("categorical", categorical),
                                ("combined", combined),
                            ]
                        )
                    )
    return pairs


def best_alignment_disorder(continuum, dissim):
    """Canonical fast=False best-alignment disorder delta(a*)."""
    return float(continuum.get_best_alignment(dissim).disorder)


# -----------------------------------------------------------------------------
# Tier 2
# -----------------------------------------------------------------------------
def choose_scale(sample_units):
    """Largest power of ten in [1e6 .. 1] keeping all scaled coords < 2^24."""
    max_coord = 0.0
    for u in sample_units:
        max_coord = max(max_coord, abs(u["start"]), abs(u["end"]))
    for exp in (6, 5, 4, 3, 2, 1, 0):
        scale = 10 ** exp
        if max_coord * scale < SCALE_MARGIN:
            return scale
    return 1


def scale_continuum(sample_units, scale):
    """Build a Continuum with coordinates * scale rounded to int."""
    scaled_units = []
    for u in sample_units:
        s = int(round(u["start"] * scale))
        e = int(round(u["end"] * scale))
        if e <= s:  # keep durations strictly positive (Segment rejects dur 0)
            e = s + 1
        scaled_units.append((u["annotator"], s, e, u["category"]))
    return scaled_units


def tier2_sampled_continua(continuum, dissim, seed):
    """Draw TIER2_SAMPLES samples; dump raw + scaled-integer replay variants."""
    sampler = StatisticalContinuumSampler()
    sampler.init_sampling(continuum)
    np.random.seed(seed)

    samples = []
    for _ in range(TIER2_SAMPLES):
        sample = sampler.sample_from_continuum  # property -> fresh Continuum
        raw_units = [
            OrderedDict(
                [
                    ("annotator", annotator),
                    ("start", float(unit.segment.start)),
                    ("end", float(unit.segment.end)),
                    ("category", unit.annotation),
                ]
            )
            for annotator in sample.annotators
            for unit in sample._annotations[annotator]
        ]
        raw_disorder = best_alignment_disorder(sample, dissim)

        scale = choose_scale(raw_units)
        scaled_unit_tuples = scale_continuum(raw_units, scale)
        scaled_continuum = build_continuum(scaled_unit_tuples)
        scaled_disorder = best_alignment_disorder(scaled_continuum, dissim)
        scaled_units = [
            OrderedDict(
                [
                    ("annotator", a),
                    ("start", int(s)),
                    ("end", int(e)),
                    ("category", cat),
                ]
            )
            for (a, s, e, cat) in scaled_unit_tuples
        ]

        samples.append(
            OrderedDict(
                [
                    ("units", raw_units),
                    ("bestAlignmentDisorder", raw_disorder),
                    (
                        "scaled",
                        OrderedDict(
                            [
                                ("scale", int(scale)),
                                ("units", scaled_units),
                                ("bestAlignmentDisorder", scaled_disorder),
                            ]
                        ),
                    ),
                ]
            )
        )
    return samples


# -----------------------------------------------------------------------------
# Tier 3
# -----------------------------------------------------------------------------
def tier3_gamma(continuum, dissim, seed):
    """compute_gamma with precision_level=None (pygamma's own 30 samples)."""
    np.random.seed(seed)
    res = continuum.compute_gamma(dissim, precision_level=None)
    return OrderedDict(
        [
            ("expectedDisorder", float(res.expected_disorder)),
            ("gamma", float(res.gamma)),
            ("nSamples", int(res.n_samples)),
        ]
    )


# -----------------------------------------------------------------------------
# Fixture assembly
# -----------------------------------------------------------------------------
def make_fixture(index, slug, description, units, seed, alpha=1.0, beta=1.0,
                 delta_empty=1.0):
    continuum = build_continuum(units)
    dissim = CombinedCategoricalDissimilarity(
        alpha=alpha, beta=beta, delta_empty=delta_empty
    )

    fixture = OrderedDict()
    fixture["metadata"] = OrderedDict(
        [
            ("pygammaVersion", PYGAMMA_VERSION),
            ("pygammaRepo", PYGAMMA_REPO),
            ("pygammaCommit", PYGAMMA_COMMIT),
            ("solver", SOLVER),
            ("seed", seed),
            ("description", description),
        ]
    )
    fixture["params"] = OrderedDict(
        [
            ("alpha", float(alpha)),
            ("beta", float(beta)),
            ("deltaEmpty", float(delta_empty)),
        ]
    )
    fixture["continuum"] = continuum_to_units(continuum)

    fixture["tier1"] = OrderedDict(
        [
            ("pairDissimilarities", tier1_pair_dissimilarities(continuum, dissim)),
            ("bestAlignmentDisorder", best_alignment_disorder(continuum, dissim)),
        ]
    )
    fixture["tier2"] = OrderedDict(
        [
            ("sampledContinua", tier2_sampled_continua(continuum, dissim, seed)),
        ]
    )
    fixture["tier3"] = tier3_gamma(continuum, dissim, seed + 1000)

    path = os.path.join(OUT_DIR, f"fixture_{index:02d}_{slug}.json")
    with open(path, "w") as f:
        json.dump(fixture, f, indent=2, sort_keys=False)
        f.write("\n")

    print(
        f"wrote {os.path.basename(path)}  "
        f"tier1.bestAlignmentDisorder={fixture['tier1']['bestAlignmentDisorder']:.6g}  "
        f"tier3.gamma={fixture['tier3']['gamma']:.6g}  "
        f"(n={fixture['tier3']['nSamples']})"
    )
    return fixture


# -----------------------------------------------------------------------------
# Hand-crafted continua
# -----------------------------------------------------------------------------
def fixture_definitions():
    defs = []

    # 1) Two annotators, simple, categories, slight positional offset.
    defs.append(
        dict(
            index=1,
            slug="two_annotators_simple",
            description="2 annotators, 3 units each, small positional offsets, "
                        "nominal categories.",
            seed=1,
            units=[
                ("Ann1", 1, 5, "a"),
                ("Ann1", 6, 10, "b"),
                ("Ann1", 11, 15, "a"),
                ("Ann2", 2, 6, "a"),
                ("Ann2", 7, 11, "b"),
                ("Ann2", 12, 16, "a"),
            ],
        )
    )

    # 2) Two annotators, unequal unit counts -> forces empty units in alignment.
    defs.append(
        dict(
            index=2,
            slug="unequal_counts",
            description="2 annotators with unequal unit counts (4 vs 2), forcing "
                        "empty units in the best alignment.",
            seed=2,
            units=[
                ("Ann1", 1, 5, "a"),
                ("Ann1", 6, 10, "b"),
                ("Ann1", 20, 24, "a"),
                ("Ann1", 30, 34, "b"),
                ("Ann2", 2, 6, "a"),
                ("Ann2", 31, 35, "b"),
            ],
        )
    )

    # 3) Two annotators, identical annotations -> observed disorder 0 -> gamma 1.
    defs.append(
        dict(
            index=3,
            slug="identical_perfect",
            description="2 annotators with identical annotations: observed "
                        "disorder is 0, so gamma is 1.",
            seed=3,
            units=[
                ("Ann1", 1, 5, "a"),
                ("Ann1", 10, 15, "b"),
                ("Ann1", 20, 25, "c"),
                ("Ann2", 1, 5, "a"),
                ("Ann2", 10, 15, "b"),
                ("Ann2", 20, 25, "c"),
            ],
        )
    )

    # 4) Three annotators.
    defs.append(
        dict(
            index=4,
            slug="three_annotators",
            description="3 annotators, 2 units each, with positional and one "
                        "categorical disagreement.",
            seed=4,
            units=[
                ("Ann1", 1, 5, "a"),
                ("Ann1", 10, 14, "b"),
                ("Ann2", 2, 6, "a"),
                ("Ann2", 11, 15, "b"),
                ("Ann3", 1, 4, "a"),
                ("Ann3", 9, 14, "c"),
            ],
        )
    )

    # 5) Larger 2-annotator continuum, ~16 units each.
    n = 16
    larger = []
    cats = ["a", "b", "c"]
    for i in range(n):
        start1 = 1 + i * 10
        larger.append(("Ann1", start1, start1 + 5, cats[i % 3]))
        start2 = 2 + i * 10  # 1-unit offset
        larger.append(("Ann2", start2, start2 + 6, cats[(i + 1) % 3]))
    defs.append(
        dict(
            index=5,
            slug="larger_two_annotators",
            description="2 annotators, 16 units each, regular offsets, cycling "
                        "categories; a larger continuum for the ILP.",
            seed=5,
            units=larger,
        )
    )

    # 6) alpha=3, beta=2 weighting to exercise the combined dissimilarity coeffs.
    defs.append(
        dict(
            index=6,
            slug="weighted_alpha3_beta2",
            description="2 annotators, 3 units each, with alpha=3 and beta=2 to "
                        "exercise positional/categorical weighting.",
            seed=6,
            alpha=3.0,
            beta=2.0,
            units=[
                ("Ann1", 1, 5, "a"),
                ("Ann1", 6, 10, "b"),
                ("Ann1", 11, 15, "c"),
                ("Ann2", 2, 7, "a"),
                ("Ann2", 8, 12, "b"),
                ("Ann2", 11, 15, "a"),
            ],
        )
    )

    return defs


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    print(f"pygamma-agreement {PYGAMMA_VERSION} (commit {PYGAMMA_COMMIT}), "
          f"solver {SOLVER}")
    print(f"output dir: {OUT_DIR}")
    for d in fixture_definitions():
        make_fixture(**d)


if __name__ == "__main__":
    main()
