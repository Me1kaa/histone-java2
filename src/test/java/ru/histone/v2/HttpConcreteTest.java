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

package ru.histone.v2;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.histone.v2.evaluator.Evaluator;
import ru.histone.v2.evaluator.resource.SchemaResourceLoader;
import ru.histone.v2.evaluator.resource.loader.DataLoader;
import ru.histone.v2.evaluator.resource.loader.FileLoader;
import ru.histone.v2.evaluator.resource.loader.HttpLoader;
import ru.histone.v2.exceptions.HistoneException;
import ru.histone.v2.parser.Parser;
import ru.histone.v2.rtti.RunTimeTypeInfo;
import ru.histone.v2.support.HistoneTestCase;
import ru.histone.v2.support.JerseyServerResource;
import ru.histone.v2.support.TestRunner;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexey Nevinsky
 */
public class HttpConcreteTest extends JerseyTestNg.ContainerPerMethodTest {
    private static final ExecutorService executor = Executors.newFixedThreadPool(20);
    private static final RunTimeTypeInfo rtti;
    private static final Evaluator evaluator;
    private static final Parser parser;

    static {
        parser = new Parser();
        evaluator = new Evaluator();
        SchemaResourceLoader loader = new SchemaResourceLoader(executor);
        loader.addLoader(SchemaResourceLoader.DATA_SCHEME, new DataLoader());
        loader.addLoader(SchemaResourceLoader.HTTP_SCHEME, new HttpLoader(executor));
        loader.addLoader(SchemaResourceLoader.FILE_SCHEME, new FileLoader());
        rtti = new RunTimeTypeInfo(executor, loader, evaluator, parser);
    }

    @Test
    public void concreteTest() throws HistoneException {

        HistoneTestCase.Case testCase = new HistoneTestCase.Case();
        testCase.setExpectedResult("GET");
        testCase.setContext(getMap());
//        testCase.setExpectedAST("[31,[25,[2,\"ab+c\",0],\"re\"],[24,[22,[21,\"re\"],\"test\"],\"ac\"]]");
        TestRunner.doTest("{{loadJSON('http://127.0.0.1:4442/', [method: 'GET']).method}}", rtti, testCase, evaluator, parser);
    }

    private Map<String, Object> getMap() {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("foo", 1L);
        values.put("bar", 2L);
        values.put("y", 3L);

        res.put("this", values);
        return res;
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "4442");

        return new ResourceConfig(JerseyServerResource.class);
    }
}
