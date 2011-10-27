/* ****************************************************************
   Copyright (C) 2004 Burr Settles, University of Wisconsin-Madison,
   Dept. of Computer Sciences and Dept. of Biostatistics and Medical
   Informatics.
   This file is part of the "ABNER (A Biomedical Named Entity
   Recognizer)" system. It requires Java 1.4. This software is
   provided "as is," and the author makes no representations or
   warranties, express or implied. For details, see the "README" file
   included in this distribution.
   This software is provided under the terms of the Common Public
   License, v1.0, as published by http://www.opensource.org. For more
   information, see the "LICENSE" file included in this distribution.
   **************************************************************** */
package edu.ucdenver.ccp.nlp.abner.ext;
import java.lang.*;
import java.io.*;
/**
   <p>ABNER's Scanner class implements the finite state machine used
   in tokenization.
   @author Burr Settles <a href="http://www.cs.wisc.edu/~bsettles">bsettles&#64;&#99;s&#46;&#119;i&#115;&#99;&#46;&#101;d&#117;</a> 
   @version 1.5 (March 2005)
*/


public class Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 256;
	private final int YY_EOF = 257;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Scanner (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Scanner (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Scanner () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
//		java.lang.System.out.print(yy_error_string[code]);
//		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NOT_ACCEPT,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NOT_ACCEPT,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NOT_ACCEPT,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,258,
"67:9,2,1,67:2,1,67:18,2,54,51,67:2,47,65,39,55,56,61,45,48,40,5,46,43:10,49" +
",53,63,62,64,54,50,36,27,30,15,34,44:2,24,22,18,44:2,17,29,25,33,44,31,19,4" +
"4:2,20,44:4,57,67,58,67,41,52,10,42,14,28,7,38,8,32,6,42:2,11,12,26,21,13,4" +
"2,16,4,9,23,3,42,35,37,42,59,66,60,67:130,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,174,
"0,1,2,3,4,5,6,7,1:4,8,1,9,1,10,1:8,11,12,1:2,2,13,14,15,1:9,16,17,16:2,18,1" +
"9,20,21,16,22,23,24,25,26,27,28,1,29,1,30,31,32,33,34,35,36,37,38,39,40,41," +
"42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,19,58,20,59,60,61,62,63,21," +
"64,65,66,67,1,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,8" +
"8,89,90,91,92,93,65,94,95,96,97,98,99,100,101,70,102,103,104,105,77,106,107" +
",108,109,110,111,64,112,113,114,115,116,117,118,119,120,121,122,123,124,125" +
",126,96,127,128,129,130,131,132,133")[0];

	private int yy_nxt[][] = unpackFromString(134,68,
"1,2,3,4,150,5,165,168,150:2,169,150:2,170,171,51,150,57,62,65,68,150,70,150" +
",72,74,76,78,80,82,84,152,172,86,88,150,90,150:2,6,173,92,150,7,94,8,9,10,1" +
"1,12,10,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,10,-1:69,29,-1:68,3" +
",-1:68,150,96,50,150:9,98,150,98:4,99,98,150,98:2,150,98,150,98:3,150,98:2," +
"150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,61:2,30,61:33,-1,61,-1," +
"61,31,61,-1:5,56,-1:21,34,-1:4,34,-1,73,34,-1:3,75,-1:22,35,-1:31,79:2,81,7" +
"9:33,36,83,85,79,52,79,37,87,58,87,-1,56,-1:66,12,-1:70,38,-1:69,16,-1:75,3" +
"9,-1,40,-1:65,41,-1:8,61:2,30,61:33,-1,61,-1,61:3,-1:5,56,-1:20,61:36,-1,61" +
",-1,61,31,61,-1:5,56,-1:20,43:2,61,43:9,59,43,59:4,43,59,43,59:2,43,59,43,5" +
"9:3,43,59:2,43,59,43:2,-1,151,-1,43:2,59,-1:5,56,-1:20,43:2,61,43:33,-1,61," +
"-1,43:3,-1:5,56,-1:20,61:2,50,61:33,-1,61,-1,61:3,-1:5,56,-1:20,61:2,83,61:" +
"33,-1,83,-1,61,46,61,-1,87,60,87,-1,56,-1:60,47,-1:27,48:2,91,48:33,-1,91,-" +
"1,48:3,-1:26,49:36,-1,49:5,-1,49,-1,49,-1:22,150:2,32,150:9,98,104,98:4,150" +
",98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92,150,101,98,102" +
",-1:4,56,-1:20,79:2,81,79:33,-1,83,85,79,52,79,-1,87,58,87,-1,56,-1:20,61:1" +
"2,93,61,93:4,61,93,61,93:2,61,93,61,93:3,61,93:2,61,93,61:2,-1,151,-1,61:2," +
"93,-1:5,56,-1:20,61:2,42,61:33,-1,61,-1,61:3,-1:5,56,-1:22,87,-1:34,87,-1:2" +
",55,-1:2,87,60,87,-1:22,91:36,-1,91,-1,91:3,-1:26,150:2,32,150,105,150:7,98" +
",106,98:4,99,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92,15" +
"0,101,98,102,-1:4,56,-1:20,61:2,32,61:33,-1,61,-1,61:3,-1:5,56,-1:20,61:36," +
"-1,61,-1,61:3,-1:5,56,-1:20,150:2,32,150:9,98,104,98:4,107,98,150,98:2,150," +
"98,150,98:3,150,98:2,150,98,150:2,33,100,92,150,101,98,102,-1:4,56,-1:22,77" +
",-1:65,43:2,61,43,54,43:31,-1,61,-1,43:3,-1:5,56,-1:20,150:2,32,150:9,98,10" +
"4,98:4,150,98,108,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92,150," +
"101,98,102,-1:4,56,-1:20,61:2,81,61:33,-1,83,-1,61,46,61,-1,87,60,87,-1,56," +
"-1:20,43:2,61,43:2,54,43:30,-1,61,-1,43:3,-1:5,56,-1:20,150:2,32,156,150:8," +
"98,150,98:4,99,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92," +
"150,101,98,102,-1:4,56,-1:20,43:2,61,43:6,54,43:26,-1,61,-1,43:3,-1:5,56,-1" +
":20,150,103,32,150:6,109,150:2,98,150,98:4,150,98,150,98:2,110,98,150,98:3," +
"150,98:2,150,98,150:2,33,100,92,150,101,98,102,-1:4,56,-1:26,34,-1:61,150:2" +
",32,150,157,150:7,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150," +
"98,150:2,33,100,92,150,101,98,102,-1:4,56,-1:28,34,-1:59,150:2,32,150:9,98," +
"150,98:4,150,98,150,98:2,111,98,150,98:3,150,98:2,150,98,150:2,33,100,92,15" +
"0,101,98,102,-1:4,56,-1:24,34,-1:63,150:2,50,150:9,98,150,98:4,150,98,150,9" +
"8:2,150,98,150,98:3,150,98:2,150,98,150:2,71,100,92,150,101,98,102,-1:4,56," +
"-1:20,63:2,-1,63:33,-1:3,63:3,-1:26,150:2,32,112,150:4,158,150:3,98,150,98:" +
"4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92,150,101,9" +
"8,102,-1:4,56,-1:20,79:2,50,79:33,-1,61,85,79:3,-1:5,56,-1:20,150:2,50,150:" +
"9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,34,100," +
"92,150,101,98,102,-1:4,56,-1:20,43:2,61,43:33,-1,61,-1,43,66,43,-1:5,56,-1:" +
"20,150:2,32,150,155,150:2,113,150:4,98,150,98:4,150,98,159,98:2,150,98,150," +
"98:3,150,98:2,150,98,150:2,33,100,92,150,101,98,102,-1:4,56,-1:20,61:36,-1," +
"61,-1,61,46,61,-1:5,56,-1:20,150:2,32,150,154,150:7,98,150,98:4,150,98,150," +
"98:2,150,98,150,98:3,114,98:2,150,98,150:2,33,100,92,150,101,98,102,-1:4,56" +
",-1:20,85:2,77,85:33,-1:2,85:4,-1:26,150:2,32,150:9,98,150,98:4,150,98,150," +
"98:2,150,98,150,98:3,115,98:2,150,98,150:2,33,100,92,150,101,98,102,-1:4,56" +
",-1:60,55,-1:27,150:2,32,150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3," +
"150,98:2,116,98,150:2,33,100,92,150,101,98,102,-1:4,56,-1:20,150:2,32,150:6" +
",117,150:2,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:" +
"2,33,100,92,150,101,98,102,-1:4,56,-1:20,92:2,77,92:33,-1,121,92:2,122,92,1" +
"02,-1:25,61:2,53,61:33,-1,61,-1,61:3,-1:5,56,-1:20,150:2,32,150:9,98,150,98" +
":4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,33,100,92,150,101," +
"98,102,-1:4,56,-1:63,153,-1:24,150:2,42,150:9,98,150,98:4,150,98,150,98:2,1" +
"50,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20" +
",150:2,50,150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98" +
",150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:5,117,150:3,98,1" +
"50,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150" +
",101,98,102,-1:4,56,-1:20,100:2,61,100:33,-1,100,121,100,123,100,102,-1:4,5" +
"6,-1:20,79:2,50,79:9,124,79,124:4,79,124,79,124:2,79,124,79,124:3,79,124:2," +
"79,124,79:2,-1,61,85,79,101,124,102,89,-1:3,56,-1:20,150,117,50,150:9,98,15" +
"0,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150," +
"101,98,102,-1:4,56,-1:20,150:2,44,150:9,98,150,98:4,150,98,150,98:2,150,98," +
"150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2" +
",50,150:9,98,150,98:4,150,98,150,98:2,150,98,117,98:3,150,98:2,150,98,150:2" +
",-1,100,92,150,101,98,102,-1:4,56,-1:20,150,104,44,150:9,98,150,98:4,150,98" +
",150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1" +
":4,56,-1:20,150:2,50,150:9,98,150,98:4,150,98,166,98:2,150,98,150,98:3,150," +
"98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98," +
"125,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,15" +
"0,101,98,102,-1:4,56,-1:20,150:2,50,150:6,126,150:2,98,150,98:4,150,98,150," +
"98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56" +
",-1:20,150:2,50,150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2," +
"150,98,150,127,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:8,112,98" +
",150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,1" +
"50,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,129,98,150,98:2,150," +
"98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,15" +
"0:2,50,150:3,117,150:5,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2" +
",150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150,133,150:7" +
",98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,9" +
"2,150,101,98,102,-1:4,56,-1:20,150:2,50,150:4,164,150:4,98,150,98:4,150,98," +
"150,98:2,150,98,150,98:3,150,98:2,150,98,134,150,-1,100,92,150,101,98,102,-" +
"1:4,56,-1:20,150:2,50,150:7,117,150,98,150,98:4,150,98,150,98:2,150,98,150," +
"98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,45," +
"150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1," +
"100,92,150,101,98,102,-1:4,56,-1:20,118:2,50,118:33,-1,100,92,118,124,118,1" +
"02,-1:4,56,-1:20,118:2,32,118:33,-1,100,92,118,124,118,102,-1:4,56,-1:20,79" +
":2,81,79:9,124,79,124:4,79,124,79,124:2,79,124,79,124:3,79,124:2,79,124,79:" +
"2,-1,83,85,79,120,124,102,87,58,87,-1,56,-1:20,121:2,-1,121:33,-1,121:3,135" +
",121,102,-1:25,85:2,77,85:9,122,85,122:4,85,122,85,122:2,85,122,85,122:3,85" +
",122:2,85,122,85:2,-1:2,85:2,122:2,102,-1:25,61:12,123,61,123:4,61,123,61,1" +
"23:2,61,123,61,123:3,61,123:2,61,123,61:2,-1,61,-1,61,123:2,102,-1:4,56,-1:" +
"20,79:2,50,79:9,124,79,124:4,79,124,79,124:2,79,124,79,124:3,79,124:2,79,12" +
"4,79:2,-1,61,85,79,124:2,102,-1:4,56,-1:20,150:2,50,150:2,117,150:6,98,150," +
"98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,10" +
"1,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,150,98,136,98:2,150,98,15" +
"0,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,5" +
"0,150:5,163,150:3,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150," +
"98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:4,137,150:4,98" +
",150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,1" +
"50,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,138,98,150,98:2,150," +
"98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,15" +
"0:2,50,150:9,98,139,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,15" +
"0:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,117,98:4,150,9" +
"8,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-" +
"1:4,56,-1:20,150:2,50,150:6,117,150:2,98,150,98:4,150,98,150,98:2,150,98,15" +
"0,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150,142" +
",50,150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2" +
",-1,100,92,150,101,98,102,-1:4,56,-1:32,135,-1,135:4,-1,135,-1,135:2,-1,135" +
",-1,135:3,-1,135:2,-1,135,-1:6,135:2,102,-1:25,150:2,50,150:9,98,150,98:4,1" +
"50,98,150,98:2,143,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,1" +
"02,-1:4,56,-1:20,150:2,50,150:3,143,150:5,98,150,98:4,150,98,150,98:2,150,9" +
"8,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150" +
":2,50,150:9,98,150,98:4,145,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150" +
":2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,150,98" +
",150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1" +
":3,95,56,-1:20,150:2,50,150:6,146,150:2,98,150,98:4,150,98,150,98:2,150,98," +
"150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2" +
",50,143,150:8,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,1" +
"50:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:6,133,150:2,98,150" +
",98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,1" +
"01,98,102,-1:4,56,-1:20,150,147,50,150:9,98,150,98:4,150,98,150,98:2,150,98" +
",150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:" +
"2,50,150:4,148,150:4,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,1" +
"50,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:8,149,98,15" +
"0,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150," +
"101,98,102,-1:4,56,-1:20,150:2,50,150:8,143,98,150,98:4,150,98,150,98:2,150" +
",98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,1" +
"50:2,50,117,150:8,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150," +
"98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,61:12,93,61,93:4,61,93,61,9" +
"3:2,61,93,61,93:3,61,93:2,61,93,61:2,-1,61,-1,61:2,93,-1:5,56,-1:20,150:2,3" +
"2,150,103,150:7,98,150,98:4,150,98,150,98:2,150,98,150,98:3,160,98:2,150,98" +
",150:2,33,100,92,150,101,98,102,-1:4,56,-1:63,97,-1:24,150:2,50,150:5,129,1" +
"50:3,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,1" +
"00,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,150,98,131,98" +
":2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-" +
"1:20,150:2,50,150:9,98,112,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,15" +
"0,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:6,128,150:2," +
"98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92" +
",150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,150,98:4,130,98,150,98:2,15" +
"0,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20," +
"150:2,50,150:3,132,150:5,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98" +
":2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150,162,150" +
":7,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100" +
",92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:7,140,150,98,150,98:4,150,98," +
"150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:" +
"4,56,-1:20,150:2,50,150:9,98,150,98:4,150,98,133,98:2,150,98,150,98:3,150,9" +
"8:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:4,144," +
"150:4,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1," +
"100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:9,98,141,98:4,150,98,150,9" +
"8:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56," +
"-1:20,150,103,64,150:9,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2" +
",150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:3,161,150" +
":5,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100" +
",92,150,101,98,102,-1:4,56,-1:20,150:2,67,150:3,96,150:5,98,150,98:4,150,98" +
",150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1" +
":4,56,-1:20,150:2,69,150:5,96,150:3,98,150,98:4,150,98,150,98:2,150,98,150," +
"98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,69," +
"150:7,96,150,98,150,98:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,15" +
"0:2,-1,100,92,150,101,98,102,-1:4,56,-1:20,150:2,50,150:4,96,150:4,98,150,9" +
"8:4,150,98,150,98:2,150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101" +
",98,102,-1:4,56,-1:20,150:2,50,150:3,167,150:5,98,150,98:4,150,98,150,98:2," +
"150,98,150,98:3,150,98:2,150,98,150:2,-1,100,92,150,101,98,102,-1:4,56,-1:2" +
"0,118:2,61,118:9,119,118,119:4,118,119,118,119:2,118,119,118,119:3,118,119:" +
"2,118,119,118:2,-1,100,92,118,120,119,102,-1:4,56,-1:17");

	public String nextToken ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			
//			System.out.println("rmap.length: " + yy_rmap.length);
//			System.out.println("state: " + yy_state);
//			System.out.println("cmap.length: " + yy_cmap.length);
//			System.out.println("look ahead: " + yy_lookahead);
			
			/* The following seems to fix arrayoutofindexexception when utf-8 characters are encountered. 
			 * Setting the next state to zero is a guess, but it seems to work. */
			if (yy_lookahead >= yy_cmap.length)
				yy_next_state = 0;
			else 
				yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return null;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{}
					case -3:
						break;
					case 3:
						{}
					case -4:
						break;
					case 4:
						{ return (yytext()); }
					case -5:
						break;
					case 5:
						{ return (yytext()); }
					case -6:
						break;
					case 6:
						{ return (yytext()); }
					case -7:
						break;
					case 7:
						{ return (yytext()); }
					case -8:
						break;
					case 8:
						{ return (yytext()); }
					case -9:
						break;
					case 9:
						{ return (yytext()); }
					case -10:
						break;
					case 10:
						{ return (yytext()); }
					case -11:
						break;
					case 11:
						{ return (yytext()); }
					case -12:
						break;
					case 12:
						{ return (yytext()); }
					case -13:
						break;
					case 13:
						{ return (yytext()); }
					case -14:
						break;
					case 14:
						{ return (yytext()); }
					case -15:
						break;
					case 15:
						{ return (yytext()); }
					case -16:
						break;
					case 16:
						{ return (yytext()); }
					case -17:
						break;
					case 17:
						{ return (yytext()); }
					case -18:
						break;
					case 18:
						{ return (yytext()); }
					case -19:
						break;
					case 19:
						{ return (yytext()); }
					case -20:
						break;
					case 20:
						{ return (yytext()); }
					case -21:
						break;
					case 21:
						{ return (yytext()); }
					case -22:
						break;
					case 22:
						{ return (yytext()); }
					case -23:
						break;
					case 23:
						{ return (yytext()); }
					case -24:
						break;
					case 24:
						{ return (yytext()); }
					case -25:
						break;
					case 25:
						{ return (yytext()); }
					case -26:
						break;
					case 26:
						{ return (yytext()); }
					case -27:
						break;
					case 27:
						{ return (yytext()); }
					case -28:
						break;
					case 28:
						{ return "/"; }
					case -29:
						break;
					case 29:
						{ return "\n"; }
					case -30:
						break;
					case 30:
						{ return (yytext()); }
					case -31:
						break;
					case 31:
						{ return (yytext()); }
					case -32:
						break;
					case 32:
						{ return (yytext()); }
					case -33:
						break;
					case 33:
						{ return (yytext()); }
					case -34:
						break;
					case 34:
						{ return (yytext()); }
					case -35:
						break;
					case 35:
						{ return "\""; }
					case -36:
						break;
					case 36:
						{ return (yytext()); }
					case -37:
						break;
					case 37:
						{ return (yytext()); }
					case -38:
						break;
					case 38:
						{ return "\""; }
					case -39:
						break;
					case 39:
						{ return (yytext()); }
					case -40:
						break;
					case 40:
						{ return (yytext()); }
					case -41:
						break;
					case 41:
						{ return (yytext()); }
					case -42:
						break;
					case 42:
						{ return (yytext()); }
					case -43:
						break;
					case 43:
						{ return (yytext()); }
					case -44:
						break;
					case 44:
						{ return (yytext()); }
					case -45:
						break;
					case 45:
						{ return (yytext()); }
					case -46:
						break;
					case 46:
						{ return (yytext()); }
					case -47:
						break;
					case 47:
						{ return (yytext()); }
					case -48:
						break;
					case 48:
						{ return (yytext()); }
					case -49:
						break;
					case 49:
						{ return (yytext()); }
					case -50:
						break;
					case 51:
						{ return (yytext()); }
					case -51:
						break;
					case 52:
						{ return (yytext()); }
					case -52:
						break;
					case 53:
						{ return (yytext()); }
					case -53:
						break;
					case 54:
						{ return (yytext()); }
					case -54:
						break;
					case 55:
						{ return (yytext()); }
					case -55:
						break;
					case 57:
						{ return (yytext()); }
					case -56:
						break;
					case 58:
						{ return (yytext()); }
					case -57:
						break;
					case 59:
						{ return (yytext()); }
					case -58:
						break;
					case 60:
						{ return (yytext()); }
					case -59:
						break;
					case 62:
						{ return (yytext()); }
					case -60:
						break;
					case 63:
						{ return (yytext()); }
					case -61:
						break;
					case 65:
						{ return (yytext()); }
					case -62:
						break;
					case 66:
						{ return (yytext()); }
					case -63:
						break;
					case 68:
						{ return (yytext()); }
					case -64:
						break;
					case 70:
						{ return (yytext()); }
					case -65:
						break;
					case 72:
						{ return (yytext()); }
					case -66:
						break;
					case 74:
						{ return (yytext()); }
					case -67:
						break;
					case 76:
						{ return (yytext()); }
					case -68:
						break;
					case 78:
						{ return (yytext()); }
					case -69:
						break;
					case 80:
						{ return (yytext()); }
					case -70:
						break;
					case 82:
						{ return (yytext()); }
					case -71:
						break;
					case 84:
						{ return (yytext()); }
					case -72:
						break;
					case 86:
						{ return (yytext()); }
					case -73:
						break;
					case 88:
						{ return (yytext()); }
					case -74:
						break;
					case 90:
						{ return (yytext()); }
					case -75:
						break;
					case 92:
						{ return (yytext()); }
					case -76:
						break;
					case 94:
						{ return (yytext()); }
					case -77:
						break;
					case 96:
						{ return (yytext()); }
					case -78:
						break;
					case 98:
						{ return (yytext()); }
					case -79:
						break;
					case 99:
						{ return (yytext()); }
					case -80:
						break;
					case 100:
						{ return (yytext()); }
					case -81:
						break;
					case 101:
						{ return (yytext()); }
					case -82:
						break;
					case 102:
						{ return (yytext()); }
					case -83:
						break;
					case 103:
						{ return (yytext()); }
					case -84:
						break;
					case 104:
						{ return (yytext()); }
					case -85:
						break;
					case 105:
						{ return (yytext()); }
					case -86:
						break;
					case 106:
						{ return (yytext()); }
					case -87:
						break;
					case 107:
						{ return (yytext()); }
					case -88:
						break;
					case 108:
						{ return (yytext()); }
					case -89:
						break;
					case 109:
						{ return (yytext()); }
					case -90:
						break;
					case 110:
						{ return (yytext()); }
					case -91:
						break;
					case 111:
						{ return (yytext()); }
					case -92:
						break;
					case 112:
						{ return (yytext()); }
					case -93:
						break;
					case 113:
						{ return (yytext()); }
					case -94:
						break;
					case 114:
						{ return (yytext()); }
					case -95:
						break;
					case 115:
						{ return (yytext()); }
					case -96:
						break;
					case 116:
						{ return (yytext()); }
					case -97:
						break;
					case 117:
						{ return (yytext()); }
					case -98:
						break;
					case 118:
						{ return (yytext()); }
					case -99:
						break;
					case 119:
						{ return (yytext()); }
					case -100:
						break;
					case 120:
						{ return (yytext()); }
					case -101:
						break;
					case 121:
						{ return (yytext()); }
					case -102:
						break;
					case 122:
						{ return (yytext()); }
					case -103:
						break;
					case 123:
						{ return (yytext()); }
					case -104:
						break;
					case 124:
						{ return (yytext()); }
					case -105:
						break;
					case 125:
						{ return (yytext()); }
					case -106:
						break;
					case 126:
						{ return (yytext()); }
					case -107:
						break;
					case 127:
						{ return (yytext()); }
					case -108:
						break;
					case 128:
						{ return (yytext()); }
					case -109:
						break;
					case 129:
						{ return (yytext()); }
					case -110:
						break;
					case 130:
						{ return (yytext()); }
					case -111:
						break;
					case 131:
						{ return (yytext()); }
					case -112:
						break;
					case 132:
						{ return (yytext()); }
					case -113:
						break;
					case 133:
						{ return (yytext()); }
					case -114:
						break;
					case 134:
						{ return (yytext()); }
					case -115:
						break;
					case 135:
						{ return (yytext()); }
					case -116:
						break;
					case 136:
						{ return (yytext()); }
					case -117:
						break;
					case 137:
						{ return (yytext()); }
					case -118:
						break;
					case 138:
						{ return (yytext()); }
					case -119:
						break;
					case 139:
						{ return (yytext()); }
					case -120:
						break;
					case 140:
						{ return (yytext()); }
					case -121:
						break;
					case 141:
						{ return (yytext()); }
					case -122:
						break;
					case 142:
						{ return (yytext()); }
					case -123:
						break;
					case 143:
						{ return (yytext()); }
					case -124:
						break;
					case 144:
						{ return (yytext()); }
					case -125:
						break;
					case 145:
						{ return (yytext()); }
					case -126:
						break;
					case 146:
						{ return (yytext()); }
					case -127:
						break;
					case 147:
						{ return (yytext()); }
					case -128:
						break;
					case 148:
						{ return (yytext()); }
					case -129:
						break;
					case 149:
						{ return (yytext()); }
					case -130:
						break;
					case 150:
						{ return (yytext()); }
					case -131:
						break;
					case 152:
						{ return (yytext()); }
					case -132:
						break;
					case 154:
						{ return (yytext()); }
					case -133:
						break;
					case 155:
						{ return (yytext()); }
					case -134:
						break;
					case 156:
						{ return (yytext()); }
					case -135:
						break;
					case 157:
						{ return (yytext()); }
					case -136:
						break;
					case 158:
						{ return (yytext()); }
					case -137:
						break;
					case 159:
						{ return (yytext()); }
					case -138:
						break;
					case 160:
						{ return (yytext()); }
					case -139:
						break;
					case 161:
						{ return (yytext()); }
					case -140:
						break;
					case 162:
						{ return (yytext()); }
					case -141:
						break;
					case 163:
						{ return (yytext()); }
					case -142:
						break;
					case 164:
						{ return (yytext()); }
					case -143:
						break;
					case 165:
						{ return (yytext()); }
					case -144:
						break;
					case 166:
						{ return (yytext()); }
					case -145:
						break;
					case 167:
						{ return (yytext()); }
					case -146:
						break;
					case 168:
						{ return (yytext()); }
					case -147:
						break;
					case 169:
						{ return (yytext()); }
					case -148:
						break;
					case 170:
						{ return (yytext()); }
					case -149:
						break;
					case 171:
						{ return (yytext()); }
					case -150:
						break;
					case 172:
						{ return (yytext()); }
					case -151:
						break;
					case 173:
						{ return (yytext()); }
					case -152:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
