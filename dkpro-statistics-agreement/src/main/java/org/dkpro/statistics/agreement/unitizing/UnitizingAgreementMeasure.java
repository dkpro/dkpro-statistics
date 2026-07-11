/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.unitizing;

import org.dkpro.statistics.agreement.DisagreementMeasure;
import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Abstract base class of agreement measures for {@link IUnitizingAnnotationStudy}s.
 *
 * @author Christian M. Meyer
 */
public abstract class UnitizingAgreementMeasure
    extends DisagreementMeasure
    implements IUnitizingAgreementMeasure
{

    protected IUnitizingAnnotationStudy study;

    /**
     * Initializes the instance for the given annotation study. The study should never be null.
     */
    public UnitizingAgreementMeasure(final IUnitizingAnnotationStudy study)
    {
        this.study = study;
    }

    @Override
    protected IAnnotationStudy getStudy()
    {
        return study;
    }

    /**
     * Unitizing measures weight all (dis)agreement by unit length. Zero-length units carry no mass,
     * so a study consisting solely of such units is degenerate: both the observed and the expected
     * disagreement are necessarily zero regardless of the positions and categories of the units.
     * Such a study must not be mistaken for full agreement.
     */
    @Override
    protected boolean studyCarriesInformation()
    {
        if (!super.studyCarriesInformation()) {
            return false;
        }

        for (IUnitizingAnnotationUnit unit : study.getUnits()) {
            if (unit.getLength() > 0) {
                return true;
            }
        }

        return false;
    }
}
