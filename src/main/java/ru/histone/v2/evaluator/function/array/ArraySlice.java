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

package ru.histone.v2.evaluator.function.array;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.EvalUtils;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.MapEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Gali Alykoff
 */
public class ArraySlice extends AbstractFunction implements Serializable {
    public static final String NAME = "slice";
    public static final int DEFAULT_OFFSET_VALUE = 0;
    public static final int DEFAULT_LEN_VALUE = 0;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        final MapEvalNode array = (MapEvalNode) args.get(0);
        final List<EvalNode> values = array.getValue().entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        final int arrayLen = values.size();
        int offset = Optional
                .ofNullable(args.size() > 1 ? args.get(1) : null)
                .flatMap(EvalUtils::tryPureIntegerValue)
                .orElse(DEFAULT_OFFSET_VALUE);
        int length = Optional
                .ofNullable(args.size() > 2 ? args.get(2) : null)
                .flatMap(EvalUtils::tryPureIntegerValue)
                .orElse(DEFAULT_LEN_VALUE);
        if (offset < 0) {
            offset = arrayLen + offset;
        }
        if (offset < 0) {
            offset = DEFAULT_OFFSET_VALUE;
        }
        if (offset > arrayLen) {
            return CompletableFuture.completedFuture(new MapEvalNode(Collections.emptyList()));
        }

        if (length == 0) {
            length = arrayLen - offset;
        }
        if (length < 0) {
            length = arrayLen - offset + length;
        }
        if (length <= 0) {
            return CompletableFuture.completedFuture(new MapEvalNode(Collections.emptyList()));
        }
        int end = offset + length;
        if (end > arrayLen) {
            end = arrayLen;
        }

        final MapEvalNode result = new MapEvalNode(values.subList(offset, end));
        return CompletableFuture.completedFuture(result);
    }
}
