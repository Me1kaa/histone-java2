package ru.histone.v2.parser;

import ru.histone.HistoneException;
import ru.histone.v2.parser.node.*;
import ru.histone.v2.utils.ParserUtils;

import java.util.*;

/**
 *
 * Created by gali.alykoff on 20/01/16.
 */
public class Marker {
    // TODO
    public void markReferences(
            AstNode rawNode, Deque<Map<String, Long>> scopeChain
    ) throws HistoneException {
        if (rawNode.hasValue()) {
            return;
        }
        if (scopeChain == null) {
            scopeChain = new LinkedList<>();
        }
        final ExpAstNode node = (ExpAstNode) rawNode;
        final AstType type = node.getType();
        switch (type) {
            case AST_REF: {
                final String nameOfVar = ((StringAstNode) node.getNode(0)).getValue();
                final ExpAstNode refNode = getReference(nameOfVar, scopeChain);
                node.rewriteNodes(Collections.singletonList(refNode));

            }
            case AST_VAR: {
                final AstNode expNode = node.getNode(0);
                final AstNode nameNode = node.getNode(1);
                markReferences(expNode, scopeChain);
                final String nameOfVar = ParserUtils.getValueFromStringNode(nameNode);
                final Long nameIndex = setReference(nameOfVar, scopeChain);
                node.setNode(1, new LongAstNode(nameIndex));
                break;
            }
            case AST_IF: {
//                int nodesSize = node.getNodes().size();
//                for (int i = 0; i < nodesSize; i += 2) {
//                    // process condition if it's present
//                    if (!Utils_isUndefined(node[c + 1]))
//                        markReferences(node[c + 1], scopeChain);
//                    // process body
//                    scopeChain.push(new HashMap<>());
//                    markReferences(node[c], scopeChain);
//                    scopeChain.pop();
//                }
                break;
            }
            case AST_FOR: break;
            case AST_MACRO: break;
            case AST_NODES:
//                scopeChain.push(new HashMap<>());
//                for (int i = 1; i < node.length; ++i)
//                    markReferences(node[c], scopeChain);
//                scopeChain.pop();
                break;
                break;
            default: break;
        }
    }

    private ExpAstNode getReference(String name, Deque<Map<String, Long>> scopeChain) {
        int scopeIndex = scopeChain.size();
        final int currentScope = scopeIndex - 1;
        final Iterator<Map<String, Long>> iterator = scopeChain.descendingIterator();
        while (iterator.hasNext()) {
            scopeIndex--;
            final Map<String, Long> scope = iterator.next();
            final Long variableIndex = scope.get(name);
            if (variableIndex != null) {
                final int scopeDeep = currentScope - scopeIndex;;
                final AstNode deepOfVarDefinitionNode = new LongAstNode(scopeDeep);
                final AstNode varIndexNode = new LongAstNode(variableIndex);
                return new ExpAstNode(AstType.AST_REF)
                        .add(deepOfVarDefinitionNode)
                        .add(varIndexNode);
            }
        }

        final AstNode globalNode = new ExpAstNode(AstType.AST_GLOBAL);
        final AstNode nameOfGlobalVarNode = new StringAstNode(name);
        return new ExpAstNode(AstType.AST_METHOD)
                .add(globalNode)
                .add(nameOfGlobalVarNode);
    }

    private Long setReference(String name, Deque<Map<String, Long>> scopeChain) {
        final Map<String, Long> lastScope = scopeChain.getLast();
        if (!lastScope.containsKey(name)) {
            lastScope.put(name, (long) lastScope.keySet().size());
        }
        return lastScope.get(name);
    }
}
