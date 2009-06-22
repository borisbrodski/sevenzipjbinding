package net.sf.sevenzipjbinding.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.IArchiveOpenVolumeCallback;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class VolumedArchiveInStream implements IInStream {
	private static final long[] DUMMY_LONG_ONE_ELEMENT_ARRAY = new long[1];

	private static final String SEVEN_ZIP_FIRST_VOLUME_POSTFIX = ".7z.001";

	private long absoluteOffset;
	private long absoluteLength = -1;

	private int currentIndex = -1;
	private IInStream currentInStream;
	private long currentVolumeOffset;
	private long currentVolumeLength;
	private List<Long> volumePositions = new ArrayList<Long>();

	private final IArchiveOpenVolumeCallback archiveOpenVolumeCallback;
	private String cuttedVolumeFilename;

	public VolumedArchiveInStream(String firstVolumeFilename, IArchiveOpenVolumeCallback archiveOpenVolumeCallback)
			throws SevenZipException {
		this.archiveOpenVolumeCallback = archiveOpenVolumeCallback;
		volumePositions.add(Long.valueOf(0));

		if (!firstVolumeFilename.endsWith(SEVEN_ZIP_FIRST_VOLUME_POSTFIX)) {
			throw new SevenZipException("The first 7z volume filename '" + firstVolumeFilename
					+ "' don't ends with the postfix: '" + SEVEN_ZIP_FIRST_VOLUME_POSTFIX + "'. Can't proceed");

		}

		cuttedVolumeFilename = firstVolumeFilename.substring(0, firstVolumeFilename.length() - 3);
		openVolume(1, true);
	}

	private int openVolume(int index, boolean seekToBegin) {
		if (currentIndex == index) {
			return 0;
		}

		for (int i = volumePositions.size(); i < index && absoluteLength == -1; i++) {
			int result = openVolume(i, false);
			if (result != 0) {
				return result;
			}
		}

		if (absoluteLength != -1 && volumePositions.size() <= index) {
			return 0;
		}

		String volumeFilename = cuttedVolumeFilename + MessageFormat.format("{0,number,000}", Integer.valueOf(index));

		// Get new IInStream
		IInStream newInStream = archiveOpenVolumeCallback.getStream(volumeFilename);

		if (newInStream == null) {
			absoluteLength = volumePositions.get(volumePositions.size() - 1).longValue();
			return 0;
		}

		currentInStream = newInStream;

		if (volumePositions.size() == index) {
			// Determine volume size
			long[] newPosition = DUMMY_LONG_ONE_ELEMENT_ARRAY;
			int result = currentInStream.seek(0, SEEK_END, newPosition);
			if (result != 0) {
				return result;
			}

			currentVolumeLength = newPosition[0];
			if (currentVolumeLength == 0) {
				throw new RuntimeException("Volume " + index + " is empty");
			}
			volumePositions.add(Long.valueOf(volumePositions.get(index - 1).longValue() + currentVolumeLength));

			if (seekToBegin) {
				result = currentInStream.seek(0, SEEK_SET, newPosition);
				if (result != 0) {
					return result;
				}
			}
		} else {
			currentVolumeLength = volumePositions.get(index).longValue() - volumePositions.get(index - 1).longValue();
		}

		if (seekToBegin) {
			currentVolumeOffset = 0;
			absoluteOffset = volumePositions.get(index - 1).longValue();
		}

		currentIndex = index;

		return 0;
	}

	// 0------X------Y------Z   length=4
	// 0      1      2      3 - list index
	// 1      2      3      4 - volume
	private int openVolumeToAbsoluteOffset() {
		int index = volumePositions.size() - 1;
		if (absoluteLength != -1 && absoluteOffset >= absoluteLength) {
			// openVolume(index, false);
			return 0;
		}
		while (volumePositions.get(index).longValue() > absoluteOffset) {
			index--;
		}

		if (index < volumePositions.size() - 1) {
			return openVolume(index + 1, false);
		}

		do {
			index++;
			int result = openVolume(index, false);
			if (result != 0) {
				return result;
			}
		} while ((absoluteLength == -1 || absoluteOffset < absoluteLength)
				&& volumePositions.get(index).longValue() <= absoluteOffset);

		//		if (volumePositions.get(index).longValue() > absoluteOffset) {
		//			return openVolume(index - 1, false);
		//		}

		return 0;
	}

	/**
	 * ${@inheritDoc}
	 */
	@Override
	public int seek(long offset, int seekOrigin, long[] newPositionOneElementArray) {
		long newOffset;
		boolean proceedWithSeek = false;
		switch (seekOrigin) {
		case SEEK_SET:
			newOffset = offset;
			break;

		case SEEK_CUR:
			newOffset = absoluteOffset + offset;
			break;

		case SEEK_END:
			if (absoluteLength == -1) {
				openVolume(Integer.MAX_VALUE, false);
				proceedWithSeek = true;
			}
			newOffset = absoluteLength + offset;
			break;

		default:
			throw new RuntimeException("Seek: unknown origin: " + seekOrigin);
		}

		System.out.println("Setting absolute offset to: " + newOffset + " (origin: " + seekOrigin + ", offset: "
				+ offset + ")");
		newPositionOneElementArray[0] = newOffset;
		if (newOffset == absoluteOffset && !proceedWithSeek) {
			return 0;
		}
		absoluteOffset = newOffset;

		int result = openVolumeToAbsoluteOffset();
		if (result != 0) {
			return result;
		}

		if (absoluteLength != -1 && absoluteLength <= absoluteOffset) {
			absoluteOffset = absoluteLength;
			newPositionOneElementArray[0] = absoluteLength;
			return 0;
		}

		currentVolumeOffset = absoluteOffset - volumePositions.get(currentIndex - 1).longValue();
		return currentInStream.seek(currentVolumeOffset, SEEK_SET, DUMMY_LONG_ONE_ELEMENT_ARRAY);
	}

	/**
	 * ${@inheritDoc}
	 */
	@Override
	public int read(byte[] data, int[] processedSizeOneElementArray) {
		if (absoluteLength != -1 && absoluteOffset >= absoluteLength) {
			processedSizeOneElementArray[0] = 0;
			return 0;
		}

		int result = currentInStream.read(data, processedSizeOneElementArray);
		if (result != 0) {
			return result;
		}

		int read = processedSizeOneElementArray[0];
		absoluteOffset += read;
		currentVolumeOffset += read;

		System.out.println("Was read: " + read + " (asked for " + data.length + ")");
		if (currentVolumeOffset >= currentVolumeLength) {
			return openVolume(currentIndex + 1, true);
		}

		return 0;
	}

}
