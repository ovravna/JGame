package game.level;

import game.entities.Entity;
import game.gfx.Light;
import game.gfx.Screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Lighting {

    public static final int BLANK = 0xfa05f0;
    public static final Integer BLACK = 0x123321;
    private HashMap<Entity, Integer[]> lightSources = new HashMap<>();
    public static final int INITIAL_FILTER = -0xaa;
    public static int filterColor = INITIAL_FILTER;
    public static int sources = 0;


    public boolean renderLight = true;
    private Screen screen;
    private int width;
    private int height;

    public Lighting(Screen screen) {

        this.screen = screen;

        width = screen.width;
        height = screen.height;

        setFilterColor();

    }

    public void renderRoundLight(int x, int y, int radius, int filter, Entity this_entity) {
        renderRoundLight(x, y, radius, filter, Light.SOFT, this_entity);
    }

    public void renderRoundLight(int x, int y, int radius, int filter, Light light, Entity this_entity) {
        renderRoundLight(x, y, radius, filter, 2, 0, light, this_entity);
    }

    public void renderRoundLight(int x, int y, int radius, int filter, int xOff, int yOff, Entity this_entity) {
        renderRoundLight(x, y, radius, filter, xOff, yOff, Light.SOFT, this_entity);
    }

    public void renderRoundLight(int x, int y, int radius, int filter, int xOffset, int yOffset, Light lighting, Entity this_entity) {

        if (!renderLight) {
            return;
        }



        Integer[] light = new Integer[width*height];

        boolean rgbFilter = false;

        int radSqrd = radius*radius;

        x -= screen.xOffset-xOffset;
        y -= screen.yOffset-yOffset;

        double distSqrd;

        int  r = -filter, g = -filter, b = -filter;
        if (Math.abs(filter) > 0xff) {
            filter = ~filter;
            r = (filter >> 16)%0x100;
            g = (filter >> 8)%0x100;
            b = filter%0x100;

            rgbFilter = true;
        }

        int rf = filterColor, gf = filterColor, bf = filterColor;
        if (Math.abs(filterColor) > 0xff) {
            filterColor = ~filterColor;
            rf = (filterColor >> 16)%0x100;
            gf = (filterColor >> 8)%0x100;
            bf = filterColor%0x100;

        }

        int xMin = x-radius < 0 ? 0:x-radius;
        int xMax = x+radius > width ? width:x+radius;
        int yMin = y-radius < 0 ? 0:y-radius;
        int yMax = y+radius > height ? height:y+radius;

        for (int xa = xMin;xa < xMax;xa++) {
            for (int ya = yMin;ya < yMax;ya++) {


                //Tegn linjer på skjermen
//                if (xa == xMin || xa == xMax-1 || ya == yMin || ya == yMax-1) {
//                    light[xa+ya*width] = BLACK;
//                    continue;
//                }

                distSqrd = (xa-x)*(xa-x) + (ya-y)*(ya-y);

                if (lighting == Light.SQUARE) {
                    light[xa+ya*width] = filter;

                } else if (distSqrd < radSqrd) {

                    if (rgbFilter && lighting == Light.SOFT) {
                        int ra = rgbCalc(r, rf, radSqrd, distSqrd);
                        int ga = rgbCalc(g, gf, radSqrd, distSqrd);
                        int ba = rgbCalc(b, bf, radSqrd, distSqrd);
                        light[xa+ya*width] = (ra << 16)+(ga << 8)+ba;

                    } else if (lighting == Light.HARD) {
                        light[xa+ya*width] = ~filter;

                    }

                } else light[xa+ya*width] = null;
            }
        }
        lightSources.put(this_entity, light);
    }

    private int rgbCalc(Integer colorValue, int filterCol, int radSqrd, double distSqrd) {

        int r = (int) (((filterColor*distSqrd)-colorValue*(radSqrd-distSqrd))/radSqrd);

        return r;
    }


    public void renderSquareLighting(int x, int y, int radius, int filter, int xOffset, int yOffset, Light lighting, Entity this_entity) {

        if (!renderLight) return;

        Integer[] light = new Integer[width*height];

        boolean rgbFilter = false;

        int radSqur = radius*radius;

        x -= screen.xOffset-xOffset;
        y -= screen.yOffset-yOffset;

        double distance;

        int a, r = 0, g = 0, b = 0;
        if (Math.abs(filter) > 0xff) {
            a = filter >> 24;
            r = filter >> 16;
            g = filter >> 8;
            b = filter;
            rgbFilter = true;
        } else a = filter;

        int xMin = x-radius < 0 ? 0:x-radius;
        int xMax = x+radius > width ? width:x+radius;
        int yMin = y-radius < 0 ? 0:y-radius;
        int yMax = y+radius > height ? height:y+radius;

        for (int xa = xMin;xa < xMax;xa++) {
            for (int ya = yMin;ya < yMax;ya++) {

                distance = (xa-x)*(xa-x)+(ya-y)*(ya-y);


                // TODO: 14.04.2016 ender kode for håndtering av filter > 0xff

                double diff = 1;

                if (lighting == Light.SOFT) {
                    diff =  ((filterColor*distance)-a*(radSqur-distance))/radSqur;
//                        diff = (distance/radSqur);
//                        System.out.println(distance);
                } else if (lighting == Light.HARD) {
                    diff = a;
                }

//                    System.out.println(temp.length);
                if (rgbFilter) {
//                        int f = (shade-a)/filterColor;
                    light[xa+ya*width] =
                            -((((int) ((r-filterColor)*diff+r)) << 16)
                                    +(((int) ((g-filterColor)*diff+g)) << 8)
                                    +(int) ((b-filterColor)*diff+b));

                } else light[xa+ya*width] = ((int) diff);
//                            -(int) ((filter-filterColor)*diff+filter);
//                            (int) (filter * Math.sin(shade) + filterColor * Math.cos(shade));

            }
        }
        lightSources.put(this_entity, light);
    }




    public void setFilterColor() {
        setFilterColor(INITIAL_FILTER);
    }

    public void setFilterColor(int filterColor) {
        Lighting.filterColor = filterColor;
    }


    public Integer lightCombiner(int i) {

        List<Entity> keys = new ArrayList<>(lightSources.keySet());
        List<Integer[]> maxes = new ArrayList<>();

        keys.sort(Collections.reverseOrder());

        Integer[] array;
        for (Entity entity : keys) {
            if ((array = lightSources.get(entity)) != null) {
                maxes.add(array);
            }
        }

        Integer max;

        if (!maxes.isEmpty()) {
            max = maxes.remove(0)[i];
        } else return null;

        Integer lightFromSource;

        sources = lightSources.size();

        for (Integer[] light : maxes) {
            lightFromSource = light[i];

            if (lightFromSource == null) continue;

            if (lightFromSource.equals(BLACK)) {
                return BLACK;
            }


            if (max == null || lightFromSource > max) {
                max = lightFromSource;
            }
        }

        return max;
    }

    public void removeLightSource(Entity entity) {
        lightSources.remove(entity);
    }

    private int clock;

    public boolean setFilter(int cycleSeconds, boolean rise, int maxFilter) {
        double time = 60*cycleSeconds;

//        filterColor = (int) (Game.getLight()*(1-Math.sin(2*(clock/time)))-maxFilter);
        int delta = (INITIAL_FILTER-maxFilter)/2;

        filterColor = (int) (delta*Math.cos((clock << 1)/time)+delta+maxFilter);

        clock++;
        if (rise) {
            return filterColor > maxFilter-10;
        } else
            return filterColor < INITIAL_FILTER;

    }

}
