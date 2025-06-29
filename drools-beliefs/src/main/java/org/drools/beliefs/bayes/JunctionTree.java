/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.kie.api.internal.assembler.ProcessedResource;
import org.kie.api.io.Resource;

import java.util.List;

public class JunctionTree implements ProcessedResource {
    private Graph<BayesVariable>    graph;
    private JunctionTreeClique      root;
    private JunctionTreeClique[]    jtNodes;
    private JunctionTreeSeparator[] jtSeps;
    private String namespace;
    private final Resource resource;
    private String name;

    public JunctionTree(Graph<BayesVariable> graph, JunctionTreeClique root, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps) {
        this( null, null, null, graph, root, jtNodes, jtSeps, true );
    }


    public JunctionTree(Resource resource, String namespace, String name, Graph<BayesVariable> graph, JunctionTreeClique root, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps) {
        this( resource, namespace, name, graph, root, jtNodes, jtSeps, true );
    }

    public JunctionTree(Resource resource, String namespace, String name, Graph<BayesVariable> graph, JunctionTreeClique root, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps, boolean init) {
        this.resource = resource;
        this.namespace = namespace;
        this.name = name;
        this.graph = graph;
        this.root = root;
        this.jtNodes = jtNodes;
        this.jtSeps = jtSeps;
        if ( init ) {
            initialize();
        }
    }

    public Graph<BayesVariable> getGraph() {
        return graph;
    }

    public JunctionTreeClique getRoot() {
        return root;
    }

    private void initialize() {
        recurseJTNodesAndInitialisePotentials( graph, root );
    }


    public void recurseJTNodesAndInitialisePotentials(Graph graph, JunctionTreeClique jtNode) {
        BayesVariable[] vars = jtNode.getValues().toArray( new BayesVariable[jtNode.getValues().size()] );

        List<BayesVariable> family = jtNode.getFamily();
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] multipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);
        for ( BayesVariable var : family ) {
            multipleVarNodePotential(graph.getNode( var.getId() ), jtNode.getPotentials(), vars, multipliers);
        }

        List<JunctionTreeSeparator> seps = jtNode.getChildren();
        for ( JunctionTreeSeparator sep : seps ) {
            recurseJTNodesAndInitialisePotentials(graph, sep.getChild());
        }
    }

    public void multipleVarNodePotential(GraphNode<BayesVariable> varNode, double[] potentials, BayesVariable[] vars, int[] multipliers ) {

        BayesVariable[] parents = new BayesVariable[varNode.getInEdges().size()];
        for ( int i = 0; i < parents.length; i++ ) {
            parents[i] = (BayesVariable) varNode.getInEdges().get(i).getOutGraphNode().getContent();
        }

        int[] parentVarPos = PotentialMultiplier.createSubsetVarPos(vars, parents);

        int parentsNumberOfStates = PotentialMultiplier.createNumberOfStates(parents);
        int[] parentIndexMultipliers = PotentialMultiplier.createIndexMultipliers(parents, parentsNumberOfStates);

        BayesVariable var = varNode.getContent();
        int varPos = -1;
        for( int i = 0; i < vars.length; i++) {
            if ( vars[i] == var)  {
                varPos = i;
                break;
            }
        }
        if ( varPos == -1 || varPos == vars.length ) {
            throw new IllegalStateException( "Unable to find Variable in set" );
        }

        PotentialMultiplier m = new PotentialMultiplier(varNode.getContent().getProbabilityTable(), varPos, parentVarPos, parentIndexMultipliers,
                                                        vars, multipliers, potentials);

        m.multiple();
    }

    public JunctionTreeClique[] getJunctionTreeNodes() {
        return jtNodes;
    }

    public JunctionTreeSeparator[] getJunctionTreeSeparators() {
        return jtSeps;
    }

    public void setJunctionTreeNodes(JunctionTreeClique[] jtNodes) {
        this.jtNodes = jtNodes;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public String getName() {
        return this.name;
    }
}
