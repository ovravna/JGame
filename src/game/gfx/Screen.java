package game.gfx;

import game.entities.Entity;
import game.level.Lighting;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Screen {

    public static final int MAP_WIDTH = 64;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH-1;

    public static final byte BIT_MIRROR_X = 0b01;
    public static final byte BIT_MIRROR_Y = 0b10;

    public static List<Integer> defaultIgnoreColors = Arrays.asList(Lighting.BLANK);

    public int[] pixels;

    public int xOffset = 0;
    public int yOffset = 0;

    public int  width;
    public int height;

    public SpriteSheet sheet;

    private Lighting lighting;
    public Graphics g;

    private Map<Entity, Map<Integer, Integer>> entityColorMap = new HashMap<>();
    private Map<Entity, List<Integer>> entityIgnoreColorMap = new HashMap<>();


    public Screen(int width, int height, SpriteSheet sheet) {
        this.width = width;
        this.height = height;
        this.sheet = sheet;

        this.pixels = new int[width*height];
    }


    public void putColorMap(Entity entity, Integer from, Integer to) {
        putColorMap(entity, new HashMap<Integer, Integer>() {{
            put(from, to);
        }});
    }

    public void putColorMap(Entity entity, Map<Integer, Integer> colorMap) {
        if (!entityColorMap.containsKey(entity)) {
            entityColorMap.put(entity, colorMap);
        } else {
            entityColorMap.get(entity).putAll(colorMap);
        }
    }

    public void putIgonreMap(Entity entity, Integer... ignore) {
        if (!entityIgnoreColorMap.containsKey(entity)) {
            entityIgnoreColorMap.put(entity, Arrays.asList(ignore));
        } else entityIgnoreColorMap.get(entity).addAll(Arrays.asList(ignore));
    }


    public void render(int xPos, int yPos, int tile, int mirrorDir, int scale, Entity e) {
        render(xPos, yPos, sheet,  tile, mirrorDir, scale, 8, e);
    }

    public void render(int xPos, int yPos, int tile, int mirrorDir, int scale, int block, Entity e) {
        render(xPos, yPos, sheet, tile, mirrorDir, scale, block, e);
    }


    public void render(int xPos, int yPos, SpriteSheet sheet, int tile, int mirrorDir, int scale, int block, Entity entity) {

        Map r = entityColorMap.get(entity);
        List l = entityIgnoreColorMap.get(entity);
        Integer fillColor = entity.fillColor;

        render(xPos, yPos, sheet, tile, mirrorDir, scale, block, l, r, fillColor);
    }

    public void render(int xPos, int yPos, int tile, int mirrorDir, int scale, int block, List<Integer> ignoreColors, HashMap<Integer, Integer> colorMap) {
        render(xPos, yPos, sheet, tile, mirrorDir, scale, block, ignoreColors, colorMap, null);
    }

    public void render(int xPos, int yPos, SpriteSheet sheet, int tile, int mirrorDir, int scale, int block, List<Integer> ignoreColors, Map<Integer, Integer> colorMap, Integer fillColor) {

        int logBlock = log2(block);


        boolean setAllColors = fillColor != null;

        boolean mapColors = colorMap != null && !setAllColors;

        if (ignoreColors == null) {
            ignoreColors = defaultIgnoreColors;
        }



        if (logBlock == -1) throw new RuntimeException("Invalid block");

        xPos -= xOffset;
        yPos -= yOffset;

        boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
        boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;

        int scaleMap = scale-1;
        int xTile = tile%(sheet.width >> logBlock);
        int yTile = tile/(sheet.width >> logBlock);
        int tileOffset = (xTile << logBlock)+(yTile << logBlock)*sheet.width;


        for (int y = 0;y < block;y++) {
            int ySheet = y;
            if (mirrorY)
                ySheet = block-y-1;

            int yPixel = y+yPos+(y*scaleMap)-((scaleMap << logBlock)/2);


            for (int x = 0;x < block;x++) {
                int xSheet = x;
                if (mirrorX)
                    xSheet = block-x-1;
                int xPixel = x+xPos+(x*scaleMap)-((scaleMap << logBlock)/2);

                Integer col = sheet.pixels[xSheet+(ySheet*sheet.width)+tileOffset];


                if (!ignoreColors.contains(col)) {

                    if (mapColors && colorMap.containsKey(col)) {
                        col = colorMap.get(col);
                    }
                    if (setAllColors) col = fillColor;
                    if (col == null || col == Lighting.BLANK) {
                        continue;
                    }

                    for (int yScale = 0;yScale < scale;yScale++) {
                        if (yPixel+yScale < 0 || yPixel+yScale >= height)
                            continue;
                        for (int xScale = 0;xScale < scale;xScale++) {
                            if (xPixel+xScale < 0 || xPixel+xScale >= width)
                                continue;
                            if (lighting.renderLight) {
                                pixels[(xPixel+xScale)+(yPixel+yScale)*width] =
                                        colorSelector(
                                                col,
                                                lighting.lightCombiner((xPixel+xScale)+(yPixel+yScale)*width)
                                        );
                            } else {
                                pixels[(xPixel+xScale)+(yPixel+yScale)*width] = col;
                            }

                        }
                    }
                }
            }
        }
    }


    public void setLighting(Lighting lighting) {
        this.lighting = lighting;
    }


    private int colorSelector(int color, Integer filter) {
        if (filter == null) {
            return colorSelector(color, lighting.filterColor);
        }
        if (filter > Math.abs(0xff)) {
            int r = (filter/0x10000)%0x100;
            int g = (filter/0x100)%0x100;
            int b = filter%0x100;

            return colorSelector(color, r, g, b);
        }

        return colorSelector(color, filter, filter, filter);
    }

    private static int colorSelector(int color, int rFilter, int gFilter, int bFilter) {

        int r = (color/0x10000)%0x100;
        int g = (color/0x100)%0x100;
        int b = color%0x100;

        List<Integer> rgb = Arrays.asList(r, g, b);
        List<Integer> filters = Arrays.asList(rFilter, gFilter, bFilter);

        for (int i = 0;i < 3;i++) {
            if (rgb.get(i) + filters.get(i) < 0) rgb.set(i, 0);
            else if (rgb.get(i) + filters.get(i) > 0xff) rgb.set(i, 0xff);
            else rgb.set(i, rgb.get(i)+filters.get(i));
        }

        return (rgb.get(0) << 16)+(rgb.get(1) << 8)+rgb.get(2);
    }

    private static int log2(int block) {
        for (int t = 1, x = 0; t <= block; t *= 2, x++) {
            if (t == block) {
                return x;
            }
        }
        return -1;
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;


    }

    
}

