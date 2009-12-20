#include <fstream>
#include <sstream>
#include <iostream>
#include <string>
#include <iomanip>

#define BLOCK_SIZE (1024 * 128)

using namespace std;


int main(int argc, char *argv[]) {
	if (argc != 4) {
		cout << "Usage:" << endl;
		cout << "  mysplit <file> <chunk size in bytes> <chunk prefix>" << endl;
		return 1;
	}

	string input_filename(argv[1]);
	string prefix(argv[3]);

	istringstream chunk_size_stream(argv[2]);
	streamsize chunk_size;
	chunk_size_stream >> chunk_size;
	if (chunk_size_stream.fail() || !chunk_size_stream.eof()) {
		cerr << "ERROR: Invalid chunk size '" << argv[2] << "'" << endl;
		return 1;
	}

	ifstream input(input_filename.c_str(), ios::binary);
	if (input.fail()) {
		cerr << "Error opening file for reading: " << input_filename << endl;
		return 1;
	}

	char * buffer = new char [BLOCK_SIZE];
	for (int chunk_number = 1; input.good() ; chunk_number++) {
		ostringstream chunk_filename;
		chunk_filename << prefix << setw(5) << setfill('0') << chunk_number;

		ofstream chunk(chunk_filename.str().c_str(), ios::binary);
		if (chunk.fail()) {
			input.close();
			delete[] buffer;
			cerr << "Error opening file for writing: " << chunk_filename.str() << endl;
			return 1;
		}

		int to_read = chunk_size;
		while (input.good() && to_read > 0) {
			int try_to_read = to_read > BLOCK_SIZE ? BLOCK_SIZE : to_read;
			input.read(buffer, try_to_read);
			streamsize read = input.gcount();
			if (read > 0) {
				to_read -= read;
				if (!chunk.write(buffer, read)) {
					input.close();
					chunk.close();
					delete[] buffer;
					cerr << "Error writing into the file: " << chunk_filename.str() << endl;
					return 1;
				}
			}
		}

		chunk.close();
	}

	delete[] buffer;
	input.close();

	return 0;
}

