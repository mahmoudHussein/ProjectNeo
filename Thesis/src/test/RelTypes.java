package test;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    ACTIV, lEAD_TO, PRECEDS, BELONGS_TO, CTR
}
