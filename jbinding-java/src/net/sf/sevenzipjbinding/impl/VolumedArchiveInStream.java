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
		currentIndex = index;

		for (int i = volumePositions.size() + 1; i < index; i++) {
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
		currentInStream = archiveOpenVolumeCallback.getStream(volumeFilename);

		if (currentInStream == null) {
			absoluteLength = volumePositions.get(volumePositions.size() - 1).longValue();
			openVolume(index - 1, seekToBegin);
			return 0;
		}

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
				currentVolumeOffset = 0;
				absoluteOffset = volumePositions.get(index - 1).longValue();
			}
		} else {
			currentVolumeLength = volumePositions.get(index).longValue() - volumePositions.get(index - 1).longValue();
		}

		return 0;
	}

	private int openVolumeToAbsoluteOffset() {
		int index = volumePositions.size() - 1;
		if (absoluteOffset == absoluteLength) {
			openVolume(index, false);
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
		} while (volumePositions.get(index).longValue() < absoluteOffset);

		if (volumePositions.get(index).longValue() > absoluteOffset) {
			return openVolume(index - 1, false);
		}

		return 0;
	}

	/**
	 * ${@inheritDoc}
	 */
	@Override
	public int seek(long offset, int seekOrigin, long[] newPositionOneElementArray) {
		long newOffset;
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
			}
			newOffset = absoluteLength + offset;

		default:
			throw new RuntimeException("Seek: unknown origin: " + seekOrigin);
		}

		if (newOffset == absoluteOffset) {
			return 0;
		}
		absoluteOffset = newOffset;

		if (absoluteLength != -1 && absoluteLength < absoluteOffset) {
			absoluteOffset = absoluteLength;
		}
		int result = openVolumeToAbsoluteOffset();
		if (result != 0) {
			return result;
		}
		currentVolumeOffset = absoluteOffset - volumePositions.get(currentIndex - 1).longValue();
		return currentInStream.seek(currentVolumeOffset, SEEK_SET, DUMMY_LONG_ONE_ELEMENT_ARRAY);
	}

	/**
	 * ${@inheritDoc}
	 */
	@Override
	public int read(byte[] data, int[] processedSizeOneElementArray) {
		if (absoluteOffset == absoluteLength) {
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

		if (currentVolumeOffset >= currentVolumeLength) {
			return openVolume(currentIndex + 1, true);
		}

		return 0;
	}

}
