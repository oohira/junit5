/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.junit.gen5.engine.FilterResult.included;

import java.util.Collection;

/**
 * Combines a collection of {@link DiscoveryFilter DiscoveryFilters} into a new
 * filter that will include elements if and only if all of the filters in the
 * specified collection include it.
 */
class CombinedDiscoveryFilter<T> implements DiscoveryFilter<T> {

	@SuppressWarnings("rawtypes")
	private static final DiscoveryFilter ALWAYS_INCLUDED_DISCOVERY_FILTER = obj -> included("Always included");

	@SuppressWarnings("unchecked")
	static <T> DiscoveryFilter<T> alwaysIncluded() {
		return ALWAYS_INCLUDED_DISCOVERY_FILTER;
	}

	private final Collection<? extends DiscoveryFilter<T>> filters;

	public CombinedDiscoveryFilter(Collection<? extends DiscoveryFilter<T>> filters) {
		this.filters = filters;
	}

	@Override
	public FilterResult filter(T element) {
		// @formatter:off
		return filters.stream()
				.map(filter -> filter.filter(element))
				.filter(FilterResult::excluded)
				.findFirst()
				.orElse(FilterResult.included("Element was included by all filters."));
		// @formatter:on
	}

	@Override
	public String toString() {
		return filters.stream().map(Object::toString).map(value -> format("(%s)", value)).collect(joining(" and "));
	}
}
