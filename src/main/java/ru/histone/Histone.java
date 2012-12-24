/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.histone.evaluator.Evaluator;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.optimizer.AstImportResolver;
import ru.histone.optimizer.AstInlineOptimizer;
import ru.histone.optimizer.AstMarker;
import ru.histone.optimizer.AstOptimizer;
import ru.histone.parser.Parser;
import ru.histone.resourceloaders.ContentType;
import ru.histone.resourceloaders.Resource;
import ru.histone.resourceloaders.ResourceLoader;
import ru.histone.utils.IOUtils;

import java.io.*;

/**
 * Main Histone engine class. Histone template parsing/evaluation is done here.<br/>
 * Histone class is thread safe!<br/>
 * You shouldn't create this class by yourself, use {@link HistoneBuilder} instead.
 *
 * @see HistoneBuilder
 */
public class Histone {
    /**
     * General Histone logger. All debug information and general logging goes here
     */
    private static final Logger log = LoggerFactory.getLogger(Histone.class);

    /**
     * Special logger for histone template syntax errors
     */
    private static final Logger RUNTIME_LOG = LoggerFactory.getLogger(Histone.class.getName() + ".RUNTIME_LOG");

    private Parser parser;
    private Evaluator evaluator;
    private NodeFactory nodeFactory;
    private AstOptimizer astAstOptimizer;
    private AstImportResolver astImportResolver;
    private AstMarker astMarker;
    private AstInlineOptimizer astInlineOptimizer;
    private ResourceLoader resourceLoader;

    public Histone(HistoneBootstrap bootstrap) {
        this.parser = bootstrap.getParser();
        this.evaluator = bootstrap.getEvaluator();
        this.nodeFactory = bootstrap.getNodeFactory();
        this.astImportResolver = bootstrap.getAstImportResolver();
        this.astMarker = bootstrap.getAstMarker();
        this.astInlineOptimizer = bootstrap.getAstInlineOptimizer();
        this.astAstOptimizer = bootstrap.getAstAstOptimizer();
        this.resourceLoader = bootstrap.getResourceLoader();
    }

    public ArrayNode parseTemplateToAST(Reader templateReader) throws HistoneException {
        String inputString = null;
        try {
            inputString = IOUtils.toString(templateReader);
        } catch (IOException e) {
            log.error("Error reading input Reader", e);
            throw new HistoneException("Error reading input Reader", e);
        }
        return parser.parse(inputString);
    }

    public ArrayNode parseTemplateToAST(String templateString) throws HistoneException {
        return parser.parse(templateString);
    }

    public ArrayNode optimizeAST(ArrayNode templateAST) throws HistoneException {
//        ArrayNode importsResolved = astImportResolver.resolve(templateAST);
//
        ArrayNode markedAst = astMarker.mark(templateAST);
//
//        ArrayNode inlinedAst = astInlineOptimizer.inline(markedAst);
//
//        ArrayNode optimizedAst = astAstOptimizer.optimize(inlinedAst);
//
//        return optimizedAst;

        return markedAst;
    }

    public String evaluateAST(ArrayNode templateAST) throws HistoneException {
        return evaluateAST(null, templateAST, NullNode.instance);
    }

    public String evaluateAST(String baseURI, ArrayNode templateAST, JsonNode context) throws HistoneException {
        return evaluator.process(baseURI, templateAST, context);
    }

    public void evaluateAST(ArrayNode templateAST, Writer output) throws HistoneException {
        evaluateAST(null, templateAST, NullNode.instance, output);
    }

    public void evaluateAST(String baseURI, ArrayNode templateAST, Writer output) throws HistoneException {
        evaluateAST(baseURI, templateAST, NullNode.instance, output);
    }

    public void evaluateAST(ArrayNode templateAST, JsonNode context, Writer output) throws HistoneException {
        evaluateAST(null, templateAST, context, output);
    }

    public void evaluateAST(String baseURI, ArrayNode templateAST, JsonNode context, Writer output) throws HistoneException {
        String result = evaluateAST(baseURI, templateAST, context);
        try {
            output.write(result);
        } catch (IOException e) {
            throw new HistoneException("Error writing to output Writer", e);
        }
    }

    public String evaluate(String baseURI, String templateContent, JsonNode context) throws HistoneException {
        ArrayNode ast = parser.parse(templateContent);
        return evaluator.process(baseURI, ast, context);
    }

    public ArrayNode evaluateAsAST(String baseURI, String templateContent, JsonNode context) throws HistoneException {
        return parser.parse(templateContent);
    }

    public String evaluateUri(String uri, JsonNode context) throws HistoneException {
        if (resourceLoader == null) throw new IllegalStateException("Resource loader is null for Histone instance");

        try {
            Resource resource = resourceLoader.load(uri, null, new String[]{ContentType.TEXT});
            String baseUri = resource.getBaseHref();

            InputStream is = resource.getInputStream();
            StringWriter sw = new StringWriter();
            IOUtils.copy(is, sw);
            String templateContent = sw.toString();
            return evaluate(baseUri, templateContent, context);
        } catch (IOException ioe) {
            throw new HistoneException(ioe);
        }
    }

    public String evaluate(String templateContent) throws HistoneException {
        return evaluate(null, templateContent, nodeFactory.jsonNull());
    }

    public String evaluate(String templateContent, JsonNode context) throws HistoneException {
        return evaluate(null, templateContent, context);
    }

    public String evaluate(Reader templateReader) throws HistoneException {
        return evaluate(null, templateReader, nodeFactory.jsonNull());
    }

    public String evaluate(String baseURI, Reader templateReader, JsonNode context) throws HistoneException {
        String templateContent = null;
        try {
            templateContent = IOUtils.toString(templateReader);
        } catch (IOException e) {
            throw new HistoneException("Error reading input Reader");
        }
        return evaluate(baseURI, templateContent, context);
    }

    public void evaluate(String baseURI, Reader templateReader, JsonNode context, Writer outputWriter) throws HistoneException {
        String result = evaluate(baseURI, templateReader, context);
        try {
            outputWriter.write(result);
        } catch (IOException e) {
            throw new HistoneException("Error writing to output Writer", e);
        }
    }

    public void setGlobalProperty(GlobalProperty property, String value) {
        evaluator.setGlobalProperty(property, value);
    }

    /**
     * Logs histone syntax error to special logger
     *
     * @param msg  message
     * @param e    exception
     * @param args arguments values that should be replaced in message
     */
    public static void runtime_log_error(String msg, Throwable e, Object... args) {
        RUNTIME_LOG.error(msg, args);
        RUNTIME_LOG.error(msg, e);
    }

    /**
     * Logs histone syntax info to special logger
     *
     * @param msg  message
     * @param args arguments values that should be replaced in message
     */
    public static void runtime_log_info(String msg, Object... args) {
        RUNTIME_LOG.info(msg, args);
    }

    /**
     * Logs histone syntax warning to special logger
     *
     * @param msg  message
     * @param args arguments values that should be replaced in message
     */

    public static void runtime_log_warn(String msg, Object... args) {
        RUNTIME_LOG.warn(msg, args);
    }

    /**
     * Logs histone syntax error to special logger
     *
     * @param msg  message
     * @param e    exception
     * @param args arguments values that should be replaced in message
     */

    public static void runtime_log_warn_e(String msg, Throwable e, Object... args) {
        RUNTIME_LOG.warn(msg, args);
    }

}
