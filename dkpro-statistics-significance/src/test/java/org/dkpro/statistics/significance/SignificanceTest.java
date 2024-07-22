/*
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
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
package org.dkpro.statistics.significance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dkpro.statistics.significance.Significance.testCorrelations;

import org.junit.jupiter.api.Test;

public class SignificanceTest
{
    @Test
    public void testTestCorrelations()
    {
        assertThat(testCorrelations(0.5, 0.74, 68, 68, 0.2)).isTrue();
        
        assertThat(testCorrelations(0.5, 0.74, 68, 68, 0.1)).isTrue();
        assertThat(testCorrelations(0.5, 0.74, 68, 68, 0.05)).isTrue();
        assertThat(testCorrelations(0.5, 0.74, 68, 68, 0.01)).isFalse();

        assertThat(testCorrelations(0.5, 0.7, 100, 100, 0.1)).isTrue();
        assertThat(testCorrelations(0.5, 0.7, 100, 100, 0.05)).isTrue();
        assertThat(testCorrelations(0.5, 0.7, 100, 100, 0.01)).isFalse();

        assertThat(testCorrelations(0.5, 0.5, 100, 100, 0.99)).isFalse();
    }
}
