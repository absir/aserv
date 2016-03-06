/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-27 下午7:12:08
 */
package com.absir.aserv.jdbc;

/**
 * @author absir
 * 
 */
public class JdbcPage {

	/** pageIndex */
	private int pageIndex;

	/** pageSize */
	private int pageSize = PAGE_SIZE;

	/** pageCount */
	private int pageCount;

	/** totalCount */
	private int totalCount;

	/** PAGE_SIZE */
	public static final int PAGE_SIZE = 20;

	/** MIN_PAGE_SIZE */
	public static final int MIN_PAGE_SIZE = 2;

	/** MAX_PAGE_SIZE */
	public static final int MAX_PAGE_SIZE = 1024;

	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * @param pageIndex
	 *            the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		if (pageSize < MIN_PAGE_SIZE) {
			pageSize = MIN_PAGE_SIZE;

		} else if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}

		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the pageCount
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount
	 *            the pageCount to set
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return the totalCount
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount
	 *            the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		pageCount = (int) Math.ceil((double) totalCount / getPageSize());
		if (pageIndex > pageCount) {
			pageIndex = pageCount;
		}

		if (pageIndex < 1) {
			pageIndex = 1;
		}

		this.totalCount = totalCount;
	}

	/**
	 * @return
	 */
	public int getFirstResult() {
		return pageSize * (pageIndex - 1);
	}
}
