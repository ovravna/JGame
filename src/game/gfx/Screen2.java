package game.gfx;

public class Screen2 {

    public static final int MAP_WIDTH = 64;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH-1;

    public static final byte BIT_MIRROR_X = 0x1;
    public static final byte BIT_MIRROR_Y = 0x2;

    public int[] pixels;

    public int xOffset = 0;
    public int yOffset = 0;

    public int width;
    public int height;

    public SpriteSheet sheet;


    public Screen2(int width, int height, SpriteSheet sheet) {
        this.width = width;
        this.height = height;
        this.sheet = sheet;

        this.pixels = new int[width*height];
    }

    public void render(int xPos, int yPos, int tile, int color, int mirrorDir, int scale) {
        render(xPos, yPos, tile, color, mirrorDir, scale, 8);
    }

    public void render(int xPos, int yPos, int tile, int color, int mirrorDir, int scale, int block) {
        render(xPos, yPos, this.sheet, tile, color, mirrorDir, scale, block);
    }


    public void render(int xPos, int yPos, SpriteSheet sheet, int tile, int color, int mirrorDir, int scale) {
        render(xPos, yPos, sheet, tile, color, mirrorDir, scale, 8);
    }

    public void render(int xPos, int yPos, SpriteSheet sheet, int tile, int color, int mirrorDir, int scale, int block) {

        int logBlock = log2(block);

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
                int col = (color >> sheet.pixels[xSheet+ySheet*sheet.width+tileOffset]*8) & 255;
                if (col < 255) {
                    for (int yScale = 0;yScale < scale;yScale++) {
                        if (yPixel+yScale < 0 || yPixel+yScale >= height)
                            continue;
                        for (int xScale = 0;xScale < scale;xScale++) {
                            if (xPixel+xScale < 0 || xPixel+xScale >= width)
                                continue;
                            pixels[(xPixel+xScale)+(yPixel+yScale)*width] = col;
                        }
                    }
                }
            }
        }
    }

    private static int log2(int block) {
        for (int t = 1, x = 0;t <= block;t *= 2, x++) {
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


//    public int[] colors = new int[MAP_WIDTH*MAP_WIDTH*4];


//    public void render(int[] pixels, int offset, int row) {
//        for (int yTile = yOffset >> 3;yTile <= (yOffset+height) >> 3;yTile++) {
//            int yMin = yTile*8-yOffset;
//            int yMax = yMin+8;
//
//            if (yMin < 0) yMin = 0;
//            if (yMax > height) yMax = height;
//
//
//            for (int xTile = xOffset >> 3;xTile <= (xOffset+width) >> 3;xTile++) {
//                int xMin = xTile*8-xOffset;
//                int xMax = xMin+8;
//
//                if (xMin < 0) xMin = 0;
//                if (xMax > width) xMax = width;
//
//                int tileIndex = (xTile & MAP_WIDTH_MASK)+(yTile & MAP_WIDTH_MASK)*MAP_WIDTH;
//
//                for (int y = yMin;y < yMax;y++) {
//                    int sheetPixel = ((y+yOffset) & 7)*sheet.width+((xMin+xOffset) & 7);
//                    int tilePixel = offset+xMin+y*row;
//                    for (int x = xMin;x < xMax;x++) {
//                        int color = tileIndex*4+sheet.pixels[sheetPixel++];
//                        pixels[tilePixel++] = colors[color];
//                    }
//                }
//            }
//        }
//    }


