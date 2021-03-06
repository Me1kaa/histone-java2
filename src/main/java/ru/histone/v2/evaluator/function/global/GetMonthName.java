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
import ru.histone.v2.evaluator.function.LocaleFunction;
import ru.histone.v2.evaluator.node.EmptyEvalNode;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.rtti.HistoneType;
import ru.histone.v2.utils.ParserUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexey Nevinsky
 */
public class GetMonthName extends LocaleFunction {

    private boolean isShort;

    public GetMonthName(boolean isShort) {
        super();
        this.isShort = isShort;
    }

    @Override
    public String getName() {
        return isShort ? "getMonthNameShort" : "getMonthNameLong";
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        return process(context, clearGlobal(args));
    }

    protected CompletableFuture<EvalNode> process(Context context, List<EvalNode> args) {
        final Properties properties = getCurrentProperties(context.getLocale());

        final Optional<Integer> idOptional = Optional.of(args)
                .filter(a -> !a.isEmpty())
                .flatMap(a -> ParserUtils.tryIntNumber(a.get(0).getValue()));
        if (!idOptional.isPresent()) {
            return CompletableFuture.completedFuture(new EmptyEvalNode());
        }
        int id = idOptional.get();

        final StringBuilder sb = new StringBuilder("MONTH_NAMES_");
        if (isShort) {
            sb.append("SHORT");
        } else {
            sb.append("LONG");
        }
        sb.append("_").append(id);
        return EvalUtils.getValue(properties.getProperty(sb.toString()));
    }
}
