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

package ru.histone.v2.evaluator.function.string;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.EvalUtils;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.StringEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Gali Alykoff
 */
public class StringSlice extends AbstractFunction {
    public static final String NAME = "slice";
    public static final int DEFALUT_START = 0;
    public static final CompletableFuture<EvalNode> EMPTY_RESULT = CompletableFuture.completedFuture(EvalUtils.createEvalNode(""));

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        final String value = ((StringEvalNode) args.get(0)).getValue();
        final int strLen = value.length();
        final Optional<Integer> startRaw = args.size() > 1
                ? EvalUtils.tryPureIntegerValue(args.get(1))
                : Optional.of(DEFALUT_START);
        final Optional<Integer> lengthRaw = args.size() > 2
                ? EvalUtils.tryPureIntegerValue(args.get(2))
                : Optional.of(strLen);
        if (!startRaw.isPresent() || !lengthRaw.isPresent()) {
            return EvalUtils.getValue(null);
        }

        int start = startRaw.get();
        if (start < 0) {
            start = strLen + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start >= strLen) {
            return EMPTY_RESULT;
        }

        int length = lengthRaw.get();
        int end = start + length;
        if (length == 0) {
            end = strLen - start;
        }
        if (length <= 0) {
            end = strLen + length;
        }
        if (start + length > strLen) {
            end = strLen;
        }

        return CompletableFuture.completedFuture(
                EvalUtils.createEvalNode(value.substring(start, end))
        );
    }
}
