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
# generate_gwet_fixtures.R
#
# PURPOSE
# -------
# Generate JSON validation fixtures for the Java implementations of Gwet's AC1
# and AC2 coefficients (dkpro-statistics-agreement: GwetAC1Agreement /
# GwetAC2Agreement). The fixtures are produced by running the reference
# implementation *irrCAC* -- authored by Kilem L. Gwet himself -- on a set of
# hand-crafted two-rater contingency tables and dumping the reference values the
# Java side asserts against:
#
#   * pa           -- (weighted) observed agreement
#   * pe           -- (weighted) chance agreement
#   * coefficient  -- the AC1 / AC2 value reported by irrCAC (coeff.val)
#
# Two cases are produced per table:
#   * AC1: unweighted -> matches GwetAC1Agreement, and GwetAC2Agreement with a
#          NominalDistanceFunction.
#   * AC2: quadratic weights -> matches GwetAC2Agreement with an
#          IntervalDistanceFunction. irrCAC's quadratic weights are
#          w_kl = 1 - (k-l)^2 / (q-1)^2, which for consecutive integer categories
#          1..q is exactly 1 - d(k,l)/d_max with d the squared difference and
#          d_max = (q-1)^2 -- i.e. the IntervalDistanceFunction weighting.
#
# PROVENANCE
# ----------
# Reference implementation exercised through its PUBLIC API only (this script
# copies no source logic from irrCAC; it merely calls gwet.ac1.raw()):
#
#   package : irrCAC == 1.4  (installed from CRAN)
#   home    : https://CRAN.R-project.org/package=irrCAC
#   author  : Kilem L. Gwet
#   license : GPL (>= 2)
#
# Because only the public API is used, the irrCAC GPL source headers do not need
# to be reproduced here. If this script is ever modified to copy algorithmic
# logic out of the irrCAC sources, the corresponding GPL notices MUST be
# reproduced and the licensing implications considered.
#
# ENVIRONMENT SETUP
# -----------------
#   R        : 4.6.1
#   packages : install.packages(c("irrCAC", "jsonlite"))
#
#   Rscript generate_gwet_fixtures.R
#
# OUTPUT
# ------
# Pretty-printed JSON files written to
#   dkpro-statistics-agreement/src/test/resources/gwet/fixture_<nn>_<slug>.json
#
# NOTE ON PRECISION
# -----------------
# pa and pe are emitted at full double precision; the coefficient is irrCAC's
# published coeff.val, which the package rounds to 5 decimals. Java asserts
# pa/pe with a tight tolerance and the coefficient with ~1e-5.
# =============================================================================

suppressMessages({
  library(irrCAC)
  library(jsonlite)
})

IRRCAC_VERSION <- as.character(packageVersion("irrCAC"))
IRRCAC_HOME <- "https://CRAN.R-project.org/package=irrCAC"
IRRCAC_LICENSE <- "GPL (>= 2)"

HERE <- tryCatch({
  # When run via Rscript, derive the script directory from the invocation.
  args <- commandArgs(trailingOnly = FALSE)
  file_arg <- sub("^--file=", "", args[grep("^--file=", args)])
  if (length(file_arg) == 1) dirname(normalizePath(file_arg)) else getwd()
}, error = function(e) getwd())

OUT_DIR <- normalizePath(file.path(HERE, "..", "resources", "gwet"),
                         mustWork = FALSE)
dir.create(OUT_DIR, showWarnings = FALSE, recursive = TRUE)

# -----------------------------------------------------------------------------
# Reconstruct a subjects x raters matrix of raw ratings from a q x q contingency
# table (rows = rater 1, cols = rater 2, categories 1..q).
# -----------------------------------------------------------------------------
table_to_raw <- function(tab) {
  q <- nrow(tab)
  rows <- list()
  for (i in seq_len(q)) {
    for (j in seq_len(q)) {
      n <- tab[i, j]
      if (n > 0) {
        rows[[length(rows) + 1]] <- matrix(rep(c(i, j), n), ncol = 2,
                                           byrow = TRUE)
      }
    }
  }
  ratings <- do.call(rbind, rows)
  colnames(ratings) <- c("rater1", "rater2")
  ratings
}

make_case <- function(ratings, weights, measure, distance_function) {
  est <- gwet.ac1.raw(ratings, weights = weights)$est
  list(
    measure = measure,
    weights = weights,
    distanceFunction = distance_function,
    pa = est$pa,
    pe = est$pe,
    coefficient = est$coeff.val
  )
}

make_fixture <- function(index, slug, description, tab) {
  q <- nrow(tab)
  ratings <- table_to_raw(tab)

  fixture <- list(
    metadata = list(
      irrcacVersion = IRRCAC_VERSION,
      irrcacHome = IRRCAC_HOME,
      irrcacLicense = IRRCAC_LICENSE,
      generatedBy = "public API (gwet.ac1.raw)",
      description = description
    ),
    categories = seq_len(q),
    table = tab,
    n = sum(tab),
    cases = list(
      make_case(ratings, "unweighted", "AC1", "nominal"),
      make_case(ratings, "quadratic", "AC2", "interval")
    )
  )

  path <- file.path(OUT_DIR, sprintf("fixture_%02d_%s.json", index, slug))
  json <- toJSON(fixture, auto_unbox = TRUE, digits = NA, pretty = TRUE,
                 matrix = "rowmajor")
  writeLines(json, path)

  ac1 <- fixture$cases[[1]]
  ac2 <- fixture$cases[[2]]
  cat(sprintf("wrote %s  AC1=%.5f  AC2(quadratic)=%.5f\n",
              basename(path), ac1$coefficient, ac2$coefficient))
}

# -----------------------------------------------------------------------------
# Hand-crafted two-rater contingency tables (rows = rater 1, cols = rater 2).
# Every category is used so that q, and hence d_max = (q-1)^2, is unambiguous.
# -----------------------------------------------------------------------------

# 1) Skewed 2x2 -- illustrates the kappa paradox (high prevalence of category 1).
make_fixture(1, "paradox_2x2",
             "Skewed 2x2 table with a high prevalence of one category; AC1 stays high where Scott's pi / kappa collapse.",
             matrix(c(80, 7,
                      8, 5), nrow = 2, byrow = TRUE))

# 2) Cohen (1960: p. 37), example 1.
make_fixture(2, "cohen_ex1",
             "3x3 table from Cohen (1960: p. 37).",
             matrix(c(25, 12, 3,
                      13, 2, 15,
                      12, 16, 2), nrow = 3, byrow = TRUE))

# 3) Cohen (1960: p. 45), example 2.
make_fixture(3, "cohen_ex2",
             "3x3 table from Cohen (1960: p. 45).",
             matrix(c(88, 14, 18,
                      10, 40, 10,
                      2, 6, 12), nrow = 3, byrow = TRUE))

# 4) 4-point ordinal (Likert) table with mass concentrated near the diagonal.
make_fixture(4, "likert_4x4",
             "4-category ordinal table; off-diagonal mass near the diagonal exercises quadratic weighting.",
             matrix(c(20, 5, 1, 0,
                      4, 15, 6, 1,
                      0, 5, 18, 4,
                      0, 1, 3, 12), nrow = 4, byrow = TRUE))

# 5) 5-point ordinal (Likert) table.
make_fixture(5, "likert_5x5",
             "5-category ordinal table; larger scale for the quadratic-weighted AC2.",
             matrix(c(15, 4, 1, 0, 0,
                      3, 12, 4, 1, 0,
                      1, 3, 14, 3, 1,
                      0, 1, 3, 10, 2,
                      0, 0, 1, 2, 8), nrow = 5, byrow = TRUE))

cat(sprintf("irrCAC %s -> %s\n", IRRCAC_VERSION, OUT_DIR))
