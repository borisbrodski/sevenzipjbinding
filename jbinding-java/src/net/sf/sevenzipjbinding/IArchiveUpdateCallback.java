//package net.sf.sevenzipjbinding;
//
///**
// * Interface to provide information for archive update operation.
// * 
// * @author Boris Brodski
// * @since 0.3
// */
//public interface IArchiveUpdateCallback {
//	/**
//	 * Query new status of item with index <code>index</code> in update list.
//	 * 
//	 * @param index
//	 *            index of item in update list to query.
//	 * @return <code>true</code> item with index <code>index</code> is new,
//	 *         otherwise <code>false</code>
//	 */
//	public boolean isNewData(int index);
//
//	public boolean hasNewProperties(int index);
//
//	public int indexInArchive(int index);
//
//	// public getUpdateItemInfo(int index,
//	// int *newData, // 1 - new data, 0 - old data
//	// int *newProperties, // 1 - new properties, 0 - old properties
//	// int *indexInArchive // -1 if there is no in archive, or if
//	// // doesn't matter
//	// );
//
//	public Object getProperty(int index, PropID propID);
//
//	public ISequentialInStream getStream(int index);
//
//	public void setOperationResult(int operationResult);
//
//}
