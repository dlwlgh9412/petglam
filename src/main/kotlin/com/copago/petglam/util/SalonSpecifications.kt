package com.copago.petglam.util

import com.copago.petglam.entity.SalonEntity
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Predicate
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.data.jpa.domain.Specification

object SalonSpecifications {
    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

    fun withDynamicQuery(searchQuery: String?, city: String?, district: String?): Specification<SalonEntity> {
        return Specification { root, query, builder ->
            val predicates = mutableListOf<Predicate>()

            searchQuery?.takeIf { it.isNotBlank() }?.let {
                val namePredicate = builder.like(builder.lower(root.get("name")), "%${it.lowercase()}%")
                predicates.add(namePredicate)
            }

            city?.takeIf { it.isNotBlank() }?.let {
                predicates.add(builder.equal(root.get<String>("city"), it))
            }

            district?.takeIf { it.isNotBlank() }?.let {
                predicates.add(builder.equal(root.get<String>("district"), it))
            }

            builder.and(*predicates.toTypedArray())
        }
    }

    fun withLocation(latitude: Double?, longitude: Double?, radiusKm: Double?): Specification<SalonEntity>? {
        if (latitude == null || longitude == null || radiusKm == null || radiusKm <= 0) {
            return null
        }

        return Specification { root, query, builder ->
            val centerPoint: Point = geometryFactory.createPoint(Coordinate(latitude, longitude))

            val locationExpression = root.get<Point>("location")

            val distanceFunction = builder.function(
                "ST_Distance_Sphere",
                Double::class.javaObjectType,
                locationExpression,
                builder.literal(centerPoint)
            )

            val radiusInMeters = radiusKm * 1000.0
            builder.lessThanOrEqualTo(distanceFunction, builder.literal(radiusInMeters))
        }
    }
}