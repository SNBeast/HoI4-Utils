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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.*;

public class MapDraw {
    public final BufferedImage map;
    //public final boolean[] provinceHasSupplyHub;
    public final Color[] provinceColors;
    public final int[] provinceStates;
    public final String[] stateOwners;
    public final Map<String, Color> tagColors = new HashMap<>();
    public final Map<Color, Color> oldToNewColor = new HashMap<>();

    public MapDraw (Path modPath) {
        map = FileIO.loadModBufferedImage(modPath, "map/provinces.bmp");

        java.util.List<String[]> definitionCSV = CSVParse.parseCSV(FileIO.loadModTextFileLines(modPath, "map/definition.csv"));
        if (definitionCSV.get(definitionCSV.size() - 1).length == 0) definitionCSV.remove(definitionCSV.size() - 1);
        definitionCSV.sort((String[] a, String[] b) -> Integer.parseInt(a[0]) - Integer.parseInt(b[0]));
        provinceColors = new Color[definitionCSV.size()];
        for (int i = 0; i < definitionCSV.size(); i++) {
            provinceColors[i] = new Color(Integer.parseInt(definitionCSV.get(i)[1]), Integer.parseInt(definitionCSV.get(i)[2]), Integer.parseInt(definitionCSV.get(i)[3]));
        }

        provinceStates = new int[provinceColors.length];
        stateOwners = new String[new File(modPath.toFile(), "history/states").list().length];
        try {
            for (Path p : (Iterable<Path>)Files.walk(modPath.resolve("history/states"))::iterator) {
                if (p.toFile().isFile()) {
                    int index = -1;
                    for (String s : Files.readAllLines(p)) {
                        if (s.matches("\\s*id\\s*=\\s*[0-9]*\\s*#*.*")) {
                            index = Integer.parseInt(s.substring(s.indexOf('=') + 1).replaceAll("#.*", "").strip());
                            break;
                        }
                    }
                    for (String s : Files.readAllLines(p)) {
                        if (s.matches("\\s*owner\\s*=\\s*[A-Za-z0-9]*\\s*#*.*")) {
                            stateOwners[index - 1] = s.substring(s.indexOf('=') + 1).replaceAll("#.*", "").strip();
                            break;
                        }
                    }
                    String stateFile = Files.readString(p);
                    java.util.List<String> provinceStrings = MiscUtils.innerScopeToStringList(stateFile, "provinces");
                    for (String s : provinceStrings) {
                        provinceStates[Integer.parseInt(s)] = index;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        provinceHasSupplyHub = new boolean[provinceColors.length];
        try {
            for (String s : Files.readAllLines(modPath.resolve("map/supply_nodes.txt"))) {
                provinceHasSupplyHub[Integer.parseInt(s.substring(s.indexOf(' ')).strip())] = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        Set<String> players = new HashSet<>();
        players.add("FKE");
        players.add("SPE");
        players.add("OMN");
        players.add("LKR");
        players.add("HBC");
        players.add("FEC");
        players.add("ENC");
        players.add("SWL");
        players.add("ENH");
        players.add("TAB");
        players.add("M27");
        players.add("HAV");

        Map<String, String> puppets = new HashMap<>();
        puppets.put("VN9", "FKE");
        puppets.put("SRC", "GNG");
        puppets.put("STA", "GNG");
        puppets.put("TAB", "GNG");
        puppets.put("TGL", "GNG");
        puppets.put("NMD", "IMP");
        puppets.put("ZAC", "IMP");
        puppets.put("WEK", "IMP");
        puppets.put("VAE", "IMP");
        puppets.put("VIR", "IMP");
        puppets.put("BRY", "IMP");
        puppets.put("VN1", "SPE");
        puppets.put("VN2", "SPE");
        puppets.put("VN3", "SPE");
        puppets.put("VN4", "SPE");
        puppets.put("VN5", "SPE");
        puppets.put("VN6", "SPE");
        puppets.put("VN7", "SPE");

        String countryColors = FileIO.loadModTextFileString(modPath, "common/countries/colors.txt");
        for (int i = 0; i < stateOwners.length; i++) {
            String s = stateOwners[i];
            try {
                if (!tagColors.containsKey(s) && s != null) {
                    if (players.contains(s)) {
                        String cutString = countryColors.substring(countryColors.indexOf(s));
                        java.util.List<String> colorStrings = MiscUtils.innerScopeToStringList(cutString, "color = rgb");
                        tagColors.put(s, new Color(Integer.parseInt(colorStrings.get(0)), Integer.parseInt(colorStrings.get(1)), Integer.parseInt(colorStrings.get(2))));
                    }
                    else if (puppets.containsKey(s) && players.contains(puppets.get(s))) {
                        String cutString = countryColors.substring(countryColors.indexOf(puppets.get(s)));
                        java.util.List<String> colorStrings = MiscUtils.innerScopeToStringList(cutString, "color = rgb");
                        tagColors.put(s, new Color(Integer.parseInt(colorStrings.get(0)), Integer.parseInt(colorStrings.get(1)), Integer.parseInt(colorStrings.get(2))));
                    }
                    else {
                        tagColors.put(s, Color.DARK_GRAY);
                    }
                }
            } catch (Exception e) {
                System.err.println(s);
                e.printStackTrace();
            }
        }

        Color darkBlue = new Color(0, 0, 64);
        for (int i = 0; i < provinceColors.length; i++) {
            if (provinceStates[i] != 0) {
                Color newColor = tagColors.get(stateOwners[provinceStates[i] - 1]);
                if (newColor == null) newColor = darkBlue;
                // else if (provinceHasSupplyHub[i]) newColor = Color.BLACK;
                oldToNewColor.put(provinceColors[i], newColor);
            }
        }

        byte[] imageData = ((DataBufferByte) map.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < imageData.length; i += 3) {
            Color oldColor = new Color(imageData[i + 2] & 0xff, imageData[i + 1] & 0xff, imageData[i] & 0xff);
            Color newColor = oldToNewColor.get(oldColor);
            if (newColor == null) newColor = darkBlue;
            imageData[i] = (byte)newColor.getBlue();
            imageData[i + 1] = (byte)newColor.getGreen();
            imageData[i + 2] = (byte)newColor.getRed();
        }

        try {
            ImageIO.write(map, "png", new File("map.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        new MapDraw(Paths.get(args[0]));
    }
}
