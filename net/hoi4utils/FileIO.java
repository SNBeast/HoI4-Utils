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

import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.*;

public class FileIO {
    private static File resolveModFile (Path modRoot, String path) {
        File resolvedFile = modRoot.resolve(path).toFile();
        if (!resolvedFile.exists()) {
            System.err.println("Fatal: \"" + path + "\" not found in the mod directory.");
            System.exit(1);
        }
        return resolvedFile;
    }

    public static BufferedImage loadModBufferedImage (Path modRoot, String path) {
        BufferedImage returnValue = null;
        try {
            returnValue = ImageIO.read(resolveModFile(modRoot, path));
        } catch (IOException e) {
            System.err.println("Fatal: Failed loading mod image file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        return returnValue;
    }
    public static List<String> loadModTextFileLines (Path modRoot, String path) {
        List<String> returnValue = null;
        try {
            returnValue = Files.readAllLines(resolveModFile(modRoot, path).toPath());
        } catch (IOException e) {
            System.err.println("Fatal: Failed reading lines of mod text file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        return returnValue;
    }
    public static String loadModTextFileString (Path modRoot, String path) {
        String returnValue = null;
        try {
            returnValue = Files.readString(resolveModFile(modRoot, path).toPath());
        } catch (IOException e) {
            System.err.println("Fatal: Failed reading mod text file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        return returnValue;
    }

    public static void overwriteModImage (Path modRoot, String path, BufferedImage image) {
        try {
            ImageIO.write(image, "BMP", resolveModFile(modRoot, path));
        } catch (IOException e) {
            System.err.println("Fatal: Failed writing/overwriting mod image file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void overwriteModTextFileLines (Path modRoot, String path, List<String> lines) {
        try {
            Files.writeString(resolveModFile(modRoot, path).toPath(), String.join("\n", lines));
        } catch (IOException e) {
            System.err.println("Fatal: Failed writing/overwriting lines to mod text file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void appendModTextFileLines (Path modRoot, String path, List<String> lines) {
        try {
            Files.writeString(resolveModFile(modRoot, path).toPath(), String.join("\n", lines), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Fatal: Failed writing/appending lines to mod text file \"" + path + "\". Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
