package processing;


import processing.core.PApplet;

import java.util.Random;


public class UsingProcessing extends PApplet
{

  Random ran = new Random( 12312348190481L );
  float zoom = 1;
  final static float inc = 1.0f;

  public static void main( String[] args )
  {
    PApplet.main( "processing.UsingProcessing" );
  }

  public void settings()
  {
    size( 1000, 1000 );
  }

  public void setup()
  {
    smooth();
    fill( 0, 0, 0 );
    rectMode( CENTER );
  }

  public void draw()
  {
    if( mousePressed )
      if( mouseButton == LEFT )
        zoom += inc;
      else if( mouseButton == RIGHT )
        zoom -= inc;

    translate( width >> 1, height >> 1 );
    scale( zoom );
    println( zoom );

    background( 0 );
    int w2 = width >> 1, h2 = height >> 1;
    fill( 255, 255, 255 );

    for( int cX = w2 * -1; cX < w2; cX++ )
    {
      for( int cZ = h2 * -1; cZ < h2; cZ++ )
      {
        fillIt( cX, cZ, cX, cZ );
      }
    }
  }

  private void fillIt( int x, int y, int a, int b )
  {
    long tModuloFactor = Math.round( Math.sin( x ) + Math.cos( y ) /* + ( ran.nextFloat() - 0.5F ) */ );
    tModuloFactor += 15;

    if( x == 0 || y == 0 )
    {
      stroke( 255 );
      point( a, b );
    }
    if( tModuloFactor != 0 && ( mod( x, tModuloFactor ) + mod( y, tModuloFactor ) == 0 ) )
    {
      stroke( 50, 150, 255 );
      point( a, b );
    }
  }

  private static long mod( long x, long y )
  {
    long result = x % y;
    return result < 0 ? result + y : result;
  }
}
