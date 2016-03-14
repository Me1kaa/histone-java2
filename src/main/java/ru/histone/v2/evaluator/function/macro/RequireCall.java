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
package ru.histone.v2.evaluator.function.macro;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.EvalUtils;
import ru.histone.v2.evaluator.data.HistoneMacro;
import ru.histone.v2.evaluator.node.EmptyEvalNode;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.RequireEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.rtti.HistoneType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author alexey.nevinsky
 */
public class RequireCall extends MacroCall {

    public static final boolean IS_UNWRAP_ARGS_ARRAYS = true;
    public static final int START_ARGS_INDEX = 1;
    public static final Optional<Integer> START_ARGS_INDEX_OPTIONAL = Optional.of(START_ARGS_INDEX);

    private static HistoneMacro getMainMacro(RequireEvalNode macroNode, List<EvalNode> args) {
        if (args.size() > 1) {
            String macroName = (String) args.get(1).getValue();
            return macroNode.getValue().get(macroName);
        }
        return null;
    }

    public static CompletableFuture<EvalNode> processRequire(
            String baseURI,
            List<EvalNode> args,
            RequireEvalNode macroNode,
            Optional<Integer> startArgsIndex,
            boolean isUnwrapArgsArrays
    ) {
        if (macroNode.getMainValue() != null) {
            if (macroNode.getMainValue().getType() == HistoneType.T_MACRO) {
                HistoneMacro macro = (HistoneMacro) macroNode.getMainValue().getValue();
                return processMacro(
                        baseURI, args, macro, startArgsIndex, isUnwrapArgsArrays
                );
            }
            return EvalUtils.getValue(macroNode.getMainValue().getValue());
        }

        final HistoneMacro histoneMacro = getMainMacro(macroNode, args);
        if (histoneMacro == null) {
            return EmptyEvalNode.FUTURE_INSTANCE;
        }

        return processMacro(
                baseURI, args, histoneMacro, startArgsIndex, isUnwrapArgsArrays
        );
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        final RequireEvalNode macroNode = (RequireEvalNode) args.get(0);
        return processRequire(
                context.getBaseUri(), args, macroNode, START_ARGS_INDEX_OPTIONAL, IS_UNWRAP_ARGS_ARRAYS
        );
    }
}
