/*
    HoI4-Utils: an assortment of utilities for handling HoI4-related files.
    Copyright (C) 2023 SNBeast

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package net.hoi4utils;

import java.util.*;

public class MiscUtils {
    public static List<String> innerScopeToStringList (String textContainingScope, String scopePreceder) {
        String beginning = textContainingScope.substring(textContainingScope.indexOf(scopePreceder));
        String[] elements = beginning.substring(beginning.indexOf('{') + 1, beginning.indexOf('}')).replaceAll("#.*", "").replaceAll("\\s+", " ").trim().split(" ");
        return new ArrayList<String>(Arrays.asList(elements));
    }

    public static String stringListToScope (List<String> stringList, String scopePreceder) {
        return scopePreceder + " = { " + String.join(" ", stringList) + " }";
    }
}
