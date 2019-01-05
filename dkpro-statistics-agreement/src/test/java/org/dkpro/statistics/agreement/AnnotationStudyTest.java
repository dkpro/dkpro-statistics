/*
 * Copyright 2014
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
package org.dkpro.statistics.agreement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.junit.Test;

public class AnnotationStudyTest
{
    @Test
    public void testAddItem()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("A", "B", "C");
        study.addItem(5, 3, 0);
        study.addItem(new Object(), "c", 12);
        study.addItem((Object[]) new String[] { "A", "B", "D" });

        assertThat(study.getItemCount()).isEqualTo(4);
    }

    @Test
    public void testAddItemMissingUnits()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> study.addItem("A"));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> study.addItem(5, 3));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> study.addItem((Object) new String[] { "A", "B", "D" }));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> study.addItem((Object[]) new String[] { "A", "B" }));
    }
}
