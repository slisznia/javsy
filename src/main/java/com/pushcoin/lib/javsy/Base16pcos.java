// Copyright (c) 2014 PushCoin, Inc.
//
// GNU General Public Licence (GPL)
// 
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
// details.
// You should have received a copy of the GNU General Public License along with
// this program; if not, write to the Free Software Foundation, Inc., 59 Temple
// Place, Suite 330, Boston, MA  02111-1307  USA
//
// __author__  = '''Slawomir Lisznianski <sl@pushcoin.com>'''

package com.pushcoin.lib.javsy;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
	PushCoin version of the Base16 codec.  Uses alphabet that 
	is friendly and less error prone to read.
*/
public class Base16pcos
{
	/**
		Prepares input for Base-16 decoding by removing
		all characters that are not part of the alphabet.
	*/
	public static String normalize( String input )
	{
		StringBuilder purified = new StringBuilder(input.length());
		// We only use upper-case letters
		String upperCase = input.toUpperCase();
		for(int i = 0, n = upperCase.length() ; i < n ; ++i)
		{ 
			char chr = upperCase.charAt(i);
			if ( Collections.binarySearch( sortedAlphabet_, chr ) >= 0) {
				purified.append( chr );
			}
		}
		return purified.toString();
	}

	public static String encode(byte[] bytes)
	{
		if (bytes == null)
			return null;

		StringBuilder printable = new StringBuilder(bytes.length * 2);

		for (int i=0; i < bytes.length; ++i) 
		{
			byte b = bytes[i];
			printable.append( alphabet_[ (int)(b & 0xf0) >> 4].charValue() );
			printable.append( alphabet_[ (int)(b & 0x0f)].charValue() );
		}      
		return printable.toString();
	}

	/**
		Decodes Base16pcos string into bytes.
		If cleanUp is true, we normalize the input first.
	*/
	public static byte[] decode(String printable, boolean cleanUp)
	{
		if (printable == null)
			return null;

		if (cleanUp) {
			printable = normalize( printable );
		}

		final int sz = printable.length();
		if( (sz % 2) != 0) {
			throw new RuntimeException( "Base16pcos decode input must be even-length: " + printable);
		}

    byte[] data = new byte[sz / 2];
    for (int i = 0; i < sz; ) 
		{
			int lft_qrt = printable.charAt(i++) - PCOS_B16_LOW_BOUND_ASCII_CH;
			int rhs_qrt = printable.charAt(i++) - PCOS_B16_LOW_BOUND_ASCII_CH;

			/* check if quarts are within bounds */
			if (lft_qrt < 0 || rhs_qrt < 0 || lft_qrt > (symbolToQuart.length-1) || rhs_qrt > (symbolToQuart.length-1) )
			{
				throw new RuntimeException( "Base16pcos decode run into an invalid character: " + printable);
			}

			SymbolDef lftp = symbolToQuart[lft_qrt];
			SymbolDef rhsp = symbolToQuart[rhs_qrt];

			// check if quart represents a valid alphabet letter
			if (! (lftp.isValid && rhsp.isValid) ) {
				throw new RuntimeException( "Base16pcos decode run into an invalid character: " + printable);
			}

			// ready to assign result
			data[i / 2] = (byte) ((lftp.value << 4) | rhsp.value);
    }
    return data;
	}

	private static final Character[] alphabet_ = {
		new Character('A'), new Character('C'),
		new Character('E'), new Character('F'),
		new Character('H'), new Character('K'),
		new Character('L'), new Character('N'),
		new Character('P'), new Character('R'),
		new Character('T'), new Character('X'),
		new Character('Y'), new Character('4'),
		new Character('5'), new Character('7')
	};

	private static final List<Character> sortedAlphabet_	= sortInplace( Arrays.asList(alphabet_) );

	private static final int PCOS_B16_LOW_BOUND_ASCII_CH = Character.digit('4', 10);

	private static class SymbolDef
	{ 
		final byte value;
		final boolean isValid;

		public SymbolDef(int v, boolean f)
		{
			value = (byte)v;
			isValid = f;
		}
	};

	private static List<Character> sortInplace(List<Character> lst)
	{
		Collections.sort(lst);
		return lst;
	}

	private static final SymbolDef[] symbolToQuart = {
		new SymbolDef( 13, true  ), new SymbolDef( 14, true ),	 // Four   Five
		new SymbolDef(  0, false ), new SymbolDef( 15, true ),	 // Six    Seven
		new SymbolDef(  0, false ), new SymbolDef(  0, false ),	 // Eight  Nine
		new SymbolDef(  0, false ), new SymbolDef(  0, false ),	 // Colon  Semicolon
		new SymbolDef(  0, false ), new SymbolDef(  0, false ),	 // Less   Equal
		new SymbolDef(  0, false ), new SymbolDef(  0, false ),	 // Gretr  ?   
		new SymbolDef(  0, false ), new SymbolDef(  0, true ),	 // @      A   
		new SymbolDef(  0, false ), new SymbolDef(  1, true ),	 // B      C   
		new SymbolDef(  0, false ), new SymbolDef(  2, true ),	 // D      E   
		new SymbolDef(  3, true  ), new SymbolDef(  0, false ),	 // F      G   
		new SymbolDef(  4, true  ), new SymbolDef(  0, false ),	 // H      I   
		new SymbolDef(  0, false ), new SymbolDef(  5, true ),	 // J      K   
		new SymbolDef(  6, true	 ), new SymbolDef(  0, false ),	 // L      M   
		new SymbolDef(  7, true	 ), new SymbolDef(  0, false ),	 // N      O   
		new SymbolDef(  8, true	 ), new SymbolDef(  0, false ),	 // P      Q   
		new SymbolDef(  9, true	 ), new SymbolDef(  0, false ),	 // R      S   
		new SymbolDef( 10, true	 ), new SymbolDef(  0, false ),	 // T      U   
		new SymbolDef(  0, false ), new SymbolDef(  0, false ),	 // V      W   
		new SymbolDef( 11, true  ), new SymbolDef( 12, true ),	 // X      Y   
	};

}
