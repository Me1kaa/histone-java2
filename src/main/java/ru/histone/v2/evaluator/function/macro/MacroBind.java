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
import ru.histone.v2.evaluator.data.HistoneMacro;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.MacroEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Gali Alykoff
 */
public class MacroBind extends AbstractFunction implements Serializable {
    public static final String NAME = "bind";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompletableFuture<EvalNode> execute(
            Context context, List<EvalNode> args
    ) throws FunctionExecutionException {
        final CompletableFuture<HistoneMacro> histoneMacro = CompletableFuture.completedFuture(
                ((MacroEvalNode) args.get(0)).getValue().clone()
        );
        return histoneMacro.thenApply(macro -> {
            final List<EvalNode> argsBindEvalNodes = new ArrayList<>();
            if (args.size() > 1) {
                argsBindEvalNodes.addAll(args.subList(1, args.size()));
            }
            macro.addBindArgs(argsBindEvalNodes);
            return new MacroEvalNode(macro);
        });
    }
}
