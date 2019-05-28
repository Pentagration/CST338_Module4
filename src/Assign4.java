/*
 * STUDENTS: Jason Pettit, Sergio Quiroz, Marcus Gonzalez,
 *           Adam Houser, Colin Reed
 * COURSE: CST 338-30_SU19
 * EXERCISE: Module 4 Optical Barcode
 */

public class Assign4
{
   public static void main(String[] args)
   {
      String[] sImageIn =
      {
         "                                               ",
         "                                               ",
         "                                               ",
         "     * * * * * * * * * * * * * * * * * * * * * ",
         "     *                                       * ",
         "     ****** **** ****** ******* ** *** *****   ",
         "     *     *    ****************************** ",
         "     * **    * *        **  *    * * *   *     ",
         "     *   *    *  *****    *   * *   *  **  *** ",
         "     *  **     * *** **   **  *    **  ***  *  ",
         "     ***  * **   **  *   ****    *  *  ** * ** ",
         "     *****  ***  *  * *   ** ** **  *   * *    ",
         "     ***************************************** ",  
         "                                               ",
         "                                               ",
         "                                               "

      };      
            
         
      
      String[] sImageIn_2 =
      {
            "                                          ",
            "                                          ",
            "* * * * * * * * * * * * * * * * * * *     ",
            "*                                    *    ",
            "**** *** **   ***** ****   *********      ",
            "* ************ ************ **********    ",
            "** *      *    *  * * *         * *       ",
            "***   *  *           * **    *      **    ",
            "* ** * *  *   * * * **  *   ***   ***     ",
            "* *           **    *****  *   **   **    ",
            "****  *  * *  * **  ** *   ** *  * *      ",
            "**************************************    ",
            "                                          ",
            "                                          ",
            "                                          ",
            "                                          "

      };
     
      BarcodeImage bc = new BarcodeImage(sImageIn);
      DataMatrix dm = new DataMatrix(bc);
     
      // First secret message
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("What a great resume builder this is!");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   } 
}

interface BarcodeIO
{
   public boolean scan(BarcodeImage bc);
   public boolean readText(String text);
   public boolean generateImageFromText();
   public boolean translateImageToText();
   public void displayTextToConsole();
   public void displayImageToConsole();

}

class BarcodeImage implements Cloneable
{
   public static final int MAX_HEIGHT = 30;    //number of rows
   public static final int MAX_WIDTH = 65;     //number of columns
   //If inbound matrix is smaller than max size, instantiate to false (blank)

   private boolean[][] imageData ;
   // White -> false
   // Black -> true

   // Default Constructor
   // Instantiates Max size array and fills with all blanks
   public BarcodeImage()
   {
      this.imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];

      for(int x = 0; x < MAX_HEIGHT; x++)
      {
         for(int y = 0; y < MAX_WIDTH; y++)
         {
            this.setPixel(x, y, false);
         }
      }
   }

   // Takes 1d string array. Converts to "the internal 2d array of booleans"
   public BarcodeImage(String[] strData)
   {
      this.imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];
      int row = MAX_HEIGHT - 1;
      for (int i = strData.length - 1; i >= 0; i--)
      {
         for (int j = 0; j < strData[i].length(); j++)
         {
            if (strData[i].charAt(j) == '*')
               imageData[row][j] = true;
            else
               imageData[row][j] = false;
         }
         --row;
      }
   }//END BARCODE IMAGE ctor
   //Constructors END

   //Individual Pixel getter
   // return is actual value and error flag. Error returns false
   public boolean getPixel(int row, int col)
   {
      if (row <= MAX_HEIGHT && col <= MAX_WIDTH)
      {
         return imageData[row][col];
      }
      else
      {
         return false;
      }
   }

   //Individual Pixel setter
   public boolean setPixel(int row, int col, boolean value)
   {
      if (row <= MAX_HEIGHT && col <= MAX_WIDTH)
      {
         imageData[row][col] = value;
         return true;
      }
      else
      {
         return false;
      }
   }

   // Optional Testing method
   public void displayToConsole()
   {
      for(int i = 0; i < MAX_HEIGHT; i++)
      {
         for(int j = 0; j < MAX_WIDTH; j++)
         {
            if (this.imageData[i][j] == true)
            {
               System.out.print("*");
            }
            else
            {
               System.out.print(" ");
            }
         }

         System.out.print("\n");
      }
   }

   // clone() overwrite cloneable method
   public Object clone()
   {
      try
      {
         return super.clone(); //simple version since type is boolean right?
      }
      catch(CloneNotSupportedException e)
      {
         return null;
      }
   }
}

class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';

   private BarcodeImage image;

   private String text;

   private int actualWidth, actualHeight;

   //defaut constructor
   DataMatrix()
   {
      this.text = "";
      this.actualHeight = 0;
      this.actualWidth = 0;
      this.image = new BarcodeImage();
   }

   //barcode constructor
   DataMatrix(BarcodeImage image)
   {
      this.text = "";
      scan(image);
   }

   //string constructor
   DataMatrix(String text)
   {
      readText(text);
      this.image = new BarcodeImage();
      generateImageFromText();
   }

   @Override
   public boolean scan(BarcodeImage bc)
   {
      try
      {
         this.image = (BarcodeImage)bc.clone();
         cleanImage();
         this.actualHeight = getActualHeight();
         this.actualWidth = getActualWidth();
         return true;
      } catch (Exception e) {
      }
      return false;
   }

   @Override
   public boolean readText(String text)
   {
      this.text = text;
      return true;
   }

   @Override
   public boolean generateImageFromText()
   {
      if (this.text.length() < BarcodeImage.MAX_WIDTH) 
      {
         char[] chars = this.text.toCharArray();

         int column = 1;
         int row = 0;

         //convert each letter to ascii then binary and set appropriately
         //1s are *, 0s are blank
         for (int j = 0; j < chars.length; j++)
         {
            //converts letter to binary
            int temp = (int) chars[j];
            String temp2 = Integer.toBinaryString(temp);
            
            //find row for top spine
            if (temp2.length() > row)
            {
               row = temp2.length();
            }

            writeCharToCol(column, temp2);

            column++;
            }

         //bottom spine
         for (int x = 0; x <= column; x++)
         {
            this.image.setPixel(BarcodeImage.MAX_HEIGHT - 1, x, true);
         }

         //leftside spine
         for (int y = 0; y <= row + 2; y++)
         {
            this.image.setPixel((BarcodeImage.MAX_HEIGHT - y - 1), 0, true);
         }

         //top spine
         for (int x = 0; x <= column; x++)
         {
            if (x % 2 == 0)
            {
               this.image.setPixel(BarcodeImage.MAX_HEIGHT - (row + 3), x, true);
            }
         }

         //rightside spine
         for (int y = 0; y <= row + 2; y++)
         {
            if (y % 2 == 0) 
            {
               this.image.setPixel((BarcodeImage.MAX_HEIGHT - y - 1), column, true);
            }
         }

         return true;
      }
      else
      {
         return false;
      }
   }
   
   public boolean writeCharToCol(int col, String code)
   {
      //add *s to the rows where binary = 1
      char[] binary = code.toCharArray();
      
      //clear image
      for (int i = 0; i < BarcodeImage.MAX_HEIGHT - 1; i++)
      {
         this.image.setPixel(i,  col,  false);
      }

      //write letter
      for (int k = 0; k < binary.length; k++)
      {
         if (binary[k] == '1')
         {
            this.image.setPixel((BarcodeImage.MAX_HEIGHT - (binary.length - k) - 1), col, true);
         }
         else
         {
            this.image.setPixel((BarcodeImage.MAX_HEIGHT - (binary.length - k) - 1),  col,  false);
         }
      }
      
      return true;
   }

   @Override
   public boolean translateImageToText()
   {
      String tempText = "";

      for (int i = 1; i < this.getActualWidth() - 1; i++)
      {
         tempText = tempText + readCharFromCol(i);
      }

      System.out.println(tempText);

      return true;
   }
   
   private char readCharFromCol(int col)
   {
      String binNumber = "";
      
      for (int i = BarcodeImage.MAX_HEIGHT - 1 - this.getActualHeight() + 2; 
            i < BarcodeImage.MAX_HEIGHT - 1; i++)
      {
         //generate binary number string
         if (this.image.getPixel(i, col) == true)
         {
            binNumber = binNumber + '1';
         }
         else
         {
            binNumber = binNumber + '0';
         }
      }
      
      //convert to ascii
      int ascii = Integer.parseInt(binNumber, 2);
      
      //convert to char
      char tempChar = (char) ascii;
      
      return tempChar;
   }

   @Override
   public void displayTextToConsole()
   {
      System.out.println(this.text);

   }

   @Override
   public void displayImageToConsole()
   {
      for(int i = BarcodeImage.MAX_HEIGHT - this.getActualHeight(); 
            i < BarcodeImage.MAX_HEIGHT; i++)
      {
         for(int j = 0; j < this.getActualWidth(); j++)
         {
            if (this.image.getPixel(i, j) == true)
            {
               System.out.print("*");
            }
            else
            {
               System.out.print(" ");
            }
         }

         System.out.print("\n");
      }
      
   }

   private int computeSignalHeight()
   {
      int height = 0;
      for (int i = BarcodeImage.MAX_HEIGHT - 1; i > 0; i--)
      {
         if (this.image.getPixel(i, 0) == false)
            break;
         ++height;
      }
      return height;
   }

   public int getActualHeight()
   {
      return computeSignalHeight();
   }

   private int computeSignalWidth()
   {
      int width = 0;
      int height = getActualHeight();
      for (int i = 0; i < BarcodeImage.MAX_WIDTH; i++)
      {
         if (image.getPixel(BarcodeImage.MAX_HEIGHT - 1, i) == false)
            break;
         ++width;
      }
      return width;
   }

   public int getActualWidth()
   {
      return computeSignalWidth();
   }

   private void cleanImage()
   {
      shiftImageDown(getBottomOffset());
      shiftImageLeft(getLeftOffset());
      
   }

   private void shiftImageDown(int offset)
   {
      for (int y = BarcodeImage.MAX_HEIGHT - 1; y > 0; y--)
      {
         for (int x = 0; x < BarcodeImage.MAX_WIDTH; x++)
         {
            if (y - offset > 0)
            {
               this.image.setPixel(y, x, this.image.getPixel(y - offset + 1, x));
               this.image.setPixel(y-offset + 1, x, false);
            }
         }
      }
   }

   private void shiftImageLeft(int offset)
   {
      for (int x = 0; x < BarcodeImage.MAX_WIDTH; x++)
      {
         for (int y = 0; y < BarcodeImage.MAX_HEIGHT; y++)
         {
            if (x + offset < BarcodeImage.MAX_WIDTH)
            {
               this.image.setPixel(y, x, this.image.getPixel(y, x + offset));
            }
         }
      }
   }
   
   public int getLeftOffset()
   {
      int leftOffset = 0;
      
      outerfor:
      for (int i = 0; i < BarcodeImage.MAX_WIDTH; i++)
      {
         innerfor:
         for (int j = 0; j < BarcodeImage.MAX_HEIGHT; j++)
         {
            leftOffset = j;
            if (this.image.getPixel(i, j) == true)
            {
               break outerfor;
            }
         }
      }
      
      return leftOffset;
   }
   
   public int getBottomOffset()
   {
      int bottomOffset = 0;
      
      outerfor:
         for (int j = BarcodeImage.MAX_HEIGHT - 1; j > 0; j--)
         {
            innerfor:
            for (int i = BarcodeImage.MAX_WIDTH - 1; i > 0; i--)
            {
               bottomOffset = BarcodeImage.MAX_HEIGHT -j;
               if (this.image.getPixel(j, i) == true)
               {
                  break outerfor;
               }
            }
         }
      
      return bottomOffset;
   }
}