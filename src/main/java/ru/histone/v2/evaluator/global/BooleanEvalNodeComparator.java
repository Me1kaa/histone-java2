/*
 * Copyright (c) 2016 MegaFon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.histone.v2.evaluator.global;

import ru.histone.v2.evaluator.node.BooleanEvalNode;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Gali Alykoff
 */
public class BooleanEvalNodeComparator implements Comparator<BooleanEvalNode>, Serializable {
    @Override
    public int compare(BooleanEvalNode left, BooleanEvalNode right) {
        return left.getValue().compareTo(right.getValue());
    }
}
