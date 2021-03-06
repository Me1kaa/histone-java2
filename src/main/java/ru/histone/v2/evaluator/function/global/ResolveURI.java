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
package ru.histone.v2.evaluator.function.global;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.EvalUtils;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.rtti.HistoneType;
import ru.histone.v2.utils.PathUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexey Nevinsky
 */
public class ResolveURI extends AbstractFunction {
    @Override
    public String getName() {
        return "resolveURI";
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        return doExecute(context, clearGlobal(args));
    }

    private CompletableFuture<EvalNode> doExecute(Context context, List<EvalNode> args) {
        //todo this is dirty hack, so needed to do edit Evaluator -> processReferenceNode
        if (args.size() > 0
                && (args.get(0).getType() == HistoneType.T_STRING || args.get(0).getType() == HistoneType.T_NUMBER)
                && !args.get(0).getValue().equals("resolveURI")) {
            String baseUri = context.getBaseUri();
            if (getValue(args, 1) != null) {
                baseUri = getValue(args, 1);
            }
            String res = PathUtils.resolveUrl(getValue(args, 0) + "", baseUri);
            return EvalUtils.getValue(res);
        }
        return EvalUtils.getValue(null);
    }
}
