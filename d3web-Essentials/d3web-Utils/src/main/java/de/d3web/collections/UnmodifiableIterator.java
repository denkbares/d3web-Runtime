package de.d3web.collections;

import java.util.Iterator;

public class UnmodifiableIterator<E> implements Iterator<E> {

	private final Iterator<E> delegate;

	public UnmodifiableIterator(Iterator<E> delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public E next() {
		return delegate.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
