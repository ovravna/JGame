package game.level;

import game.entities.Entity;
import game.gfx.Light;
import game.gfx.Screen;

import java.util.HashMap;


public class Lighting {

    private HashMap<Entity, Integer[]> lightSources = new HashMap<>();
    public static final int INITIAL_FILTER = -0xdf;
    public static int filterColor = INITIAL_FILTER;

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

        int alpha, r = 0, g = 0, b = 0;
        if (Math.abs(filter) > 0xff) {
            alpha = filter >> 24;
            r = filter >> 16;
            g = filter >> 8;
            b = filter;
            rgbFilter = true;

        } else alpha = filter;

        int xMin = x-radius < 0 ? 0:x-radius;
        int xMax = x+radius > width ? width:x+radius;
        int yMin = y-radius < 0 ? 0:y-radius;
        int yMax = y+radius > height ? height:y+radius;

        for (int xa = xMin;xa < xMax;xa++) {
            for (int ya = yMin;ya < yMax;ya++) {

                distSqrd = (xa-x)*(xa-x) + (ya-y)*(ya-y);

                // TODO: 14.04.2016 ender kode for håndtering av filter > 0xff
                if (distSqrd < radSqrd) {

                    double diff = 1;

                    if (lighting == Light.SOFT) {
                        diff = ((int) ((filterColor*distSqrd)-alpha*(radSqrd-distSqrd))/radSqrd);
//                        diff = (distance/radSqur);
//                        System.out.println(distance);
                    } else if (lighting == Light.HARD) {
                        diff = alpha;
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

                } else light[xa+ya*width] = null;
            }
        }
        lightSources.put(this_entity, light);
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
        this.filterColor = filterColor;
    }

    private static final int INITIAL_R = -0xffffff;
    public static int sources = 0;

    public Integer lightCombiner(int i) {

        Integer r = INITIAL_R;
        Integer temp;

        sources = lightSources.size();

        for (Integer[] light : lightSources.values()) {
            temp = light[i];

            if (temp != null) {
                r = temp > r ? temp > filterColor ? temp:filterColor:r;
            }
        }

        if (r == INITIAL_R) {
            r = null;
        }


        return r;
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
