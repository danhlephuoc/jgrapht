/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Lead:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2005, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -----------------
 * AdaptiveIsomorphismInspectorFactory.java
 * -----------------
 * (C) Copyright 2005, by Assaf Lehr and Contributors.
 *
 * Original Author:  Assaf Lehr
 * Contributor(s):   -
 *
 * Changes
 * -------
 */
package org._3pq.jgrapht.alg.isomorphism;

import org._3pq.jgrapht.*;
import org._3pq.jgrapht.graph.*;
import org._3pq.jgrapht.util.equivalence.*;


/**
 * This class serves as a factory for GraphIsomorphismInspector concrete
 * implementations. It can be used in two ways:
 * <li>You can can let this class to determine what is the most efficient
 * algorithm for your graph.
 * <li>You can specify the type of your graph (planar / tree / other) and save
 * this class the graph-checking time.
 *
 * <p>Note that the concrete implementations are package-private and should not
 * be created directly.  If you are the maintainer of the package, you can add
 * new implementation classes, and add them to the "check-list". The current
 * algorithms do not support graphs with multiple edges (Multigraph /
 * Pseudograph)
 *
 * @author Assaf
 * @see GraphIsomorphismInspector
 * @since Jul 17, 2005
 */
public class AdaptiveIsomorphismInspectorFactory
{

    //~ Static fields/initializers --------------------------------------------

    // REVIEW jvs 5-Sept-2005:  the term "regular" has a specific meaning in
    // graph theory (a graph where every vertex has the same degree).  I don't
    // think that's what you mean here; probably should be
    // GRAPH_TYPE_ARBITRARY instead?
    public static final int GRAPH_TYPE_REGULAR = 0;
    public static final int GRAPH_TYPE_PLANAR = 1;
    public static final int GRAPH_TYPE_TREE = 2;
    public static final int GRAPH_TYPE_MULTIGRAPH = 3;

    //~ Methods ---------------------------------------------------------------

    /**
     * Creates a new inspector, letting this class determine what is the most
     * efficient algorithm.
     *
     * @param graph1
     * @param graph2
     * @param vertexChecker may be null
     * @param edgeChecker may be null
     */
    public static GraphIsomorphismInspector createIsomorphismInspector(
        Graph graph1,
        Graph graph2,
        EquivalenceComparator vertexChecker,
        EquivalenceComparator edgeChecker)
    {
        int graphType = checkGraphsType(graph1, graph2);
        return
            createAppropriateConcreteInspector(
                graphType,
                graph1,
                graph2,
                vertexChecker,
                edgeChecker);
    }

    /**
     * Creates a new inspector, letting this class determine what is the most
     * efficient algorithm and using default equivalence comparators.
     *
     * <p>same as calling createIsomorphismInspector(graph1,graph2,null,null);
     *
     * @param graph1
     * @param graph2
     */
    public static GraphIsomorphismInspector createIsomorphismInspector(
        Graph graph1,
        Graph graph2)
    {
        return createIsomorphismInspector(graph1, graph2, null, null);
    }

    /**
     * Creates a new inspector for a particular graph type (planar / tree /
     * other).
     *
     * @param type - AdaptiveIsomorphismInspectorFactory.GRAPH_TYPE_XXX
     * @param graph1
     * @param graph2
     * @param vertexChecker - can be null
     * @param edgeChecker - can be null
     */
    public static GraphIsomorphismInspector createIsomorphismInspectorByType(
        int type,
        Graph graph1,
        Graph graph2,
        EquivalenceComparator vertexChecker,
        EquivalenceComparator edgeChecker)
    {
        return
            createAppropriateConcreteInspector(
                type,
                graph1,
                graph2,
                vertexChecker,
                edgeChecker);
    }

    /**
     * Creates a new inspector for a particular graph type (planar / tree /
     * other) using default equivalence comparators.
     *
     * <p>same as calling
     * createAppropriateConcreteInspector(graph1,graph2,null,null);
     *
     * @param type - AdaptiveIsomorphismInspectorFactory.GRAPH_TYPE_XXX
     * @param graph1
     * @param graph2
     */
    public static GraphIsomorphismInspector createIsomorphismInspectorByType(
        int type,
        Graph graph1,
        Graph graph2)
    {
        return
            createAppropriateConcreteInspector(
                type,
                graph1,
                graph2,
                null,
                null);
    }

    /**
     * Checks the graph type, and accordingly decides which type of concrete
     * inspector class to create. This implementation creates an exhaustive
     * inspector without further tests, because no other implementations are
     * available yet.
     *
     * @param graph1
     * @param graph2
     * @param vertexChecker
     * @param edgeChecker
     */
    protected static GraphIsomorphismInspector
    createAppropriateConcreteInspector(
        int graphType,
        Graph graph1,
        Graph graph2,
        EquivalenceComparator vertexChecker,
        EquivalenceComparator edgeChecker)
    {
        assertUnsupportedGraphTypes(graph1, graph2);
        GraphIsomorphismInspector currentInspector = null;

        switch (graphType) {
        case GRAPH_TYPE_PLANAR:
        case GRAPH_TYPE_TREE:
        case GRAPH_TYPE_REGULAR:
            currentInspector =
                createTopologicalExhaustiveInspector(
                    graph1,
                    graph2,
                    vertexChecker,
                    edgeChecker);
            break;

        default:

        // REVIEW jvs 5-Sept-2005:  Why is this commented out?  Shouldn't
        // it be returning something instead of null?
        // currentInspector = new
        // EquivalenceIsomorphismInspector(graph1,graph2,vertexChecker,edgeChecker);
        }
        return currentInspector;
    }

    /**
     * Checks if one of the graphs is from unsupported graph type and throws
     * IllegalArgumentException if it is. The current unsupported types are
     * graphs with multiple-edges.
     *
     * @param graph1
     * @param graph2
     *
     * @throws IllegalArgumentException
     */
    protected static void assertUnsupportedGraphTypes(
        Graph graph1,
        Graph graph2)
        throws IllegalArgumentException
    {
        Graph [] graphArray = new Graph [] {
                graph1, graph2
            };
        for (int i = 0; i < graphArray.length; i++) {
            Graph g = graphArray[i];
            if ((g instanceof Multigraph)
                || (g instanceof DirectedMultigraph)
                || (g instanceof Pseudograph)) {
                throw new IllegalArgumentException(
                    "graph type not supported for the graph" + g);
            }
        }
    }

    protected static int checkGraphsType(Graph graph1, Graph graph2)
    {
        return GRAPH_TYPE_REGULAR;
    }

    /**
     * @return ExhaustiveInspector, where the equivalence comparator is chained
     *         with a topological comparator. This implementation uses:
     *         <li>vertex degree size comparator
     */
    protected static GraphIsomorphismInspector
    createTopologicalExhaustiveInspector(
        Graph graph1,
        Graph graph2,
        EquivalenceComparator vertexChecker,
        EquivalenceComparator edgeChecker)
    {
        VertexDegreeEquivalenceComparator degreeComparator =
            new VertexDegreeEquivalenceComparator();
        EquivalenceComparatorChain vertexChainedChecker =
            new EquivalenceComparatorChainBase(degreeComparator);
        vertexChainedChecker.addComparatorAfter(vertexChecker);

        GraphIsomorphismInspector inspector =
            new EquivalenceIsomorphismInspector(
                graph1,
                graph2,
                vertexChainedChecker,
                edgeChecker);
        return inspector;
    }
}