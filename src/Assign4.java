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
      String[] sImageIn = new String[]
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
      
      BarcodeImage bc = new BarcodeImage(sImageIn);
      bc.displayToConsole();
      DataMatrix dm = new DataMatrix(bc);
      System.out.println("LeftOffset");
      System.out.println(dm.getLeftOffset());
      System.out.println("BottomOffset");
      System.out.println(dm.getBottomOffset());
     
      // First secret message
      //dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   }
}

interface BarcodeIO
{
   // All Implimentations expected to store img and text
   //BarcodeImage img;

   //String text;

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


   // Constructors (2 minimum)

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
      //add borders
      /*
      for (int i = MAX_HEIGHT - 1; i >= 0; i--)
      {
        for (int j = MAX_WIDTH - 1; j >= 0; j--)
        {
            if (i == 0)
               imageData[i][j] = true;
            else if (i == MAX_HEIGHT - 1)
               imageData[i][j] = true;
            else if (j ==0)
               imageData[i][j] = true;
            else if (j == MAX_WIDTH - 1)
               imageData[i][j] = true;
        }
      }*/
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


   // Optional function
   // Checks incoming data for "very conceivable size or null error"
   private boolean checkSize(String[] data)
   {
      int size = 0;

      for (int i = 0; i < data.length; i++)
      {
         size = size + data[i].length();
      }

      if(size > MAX_WIDTH || data == null) //should this be -1?
      {
         return false;
      }
      else
      {
         return true;
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
         this.actualHeight = getActualHeight();
         this.actualWidth = getActualWidth();
         cleanImage();//not implemented yet
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
      //convert sentence to string array (removes spaces)
      //odd indicies get a space
      if (this.text.length() < BarcodeImage.MAX_WIDTH) 
      {
         //String[] tempArray = this.text.split(" ");
         
         char[] chars = this.text.toCharArray();

         int column = 1;
         int row = 0;

         //convert each letter to ascii then binary and set appropriately
         //1s are *, 0s are blank
         //for (int i = 0; i < tempArray.length; i++) // columns
         //{
            //pull word into character array
            //char[] chars = tempArray[i].toCharArray();

            for (int j = 0; j < chars.length; j++)
            {
               //converts letter to binary
               int temp = (int) chars[j];
               String temp2 = Integer.toBinaryString(temp);

               //add *s to the rows where binary = 1
               char[] binary = temp2.toCharArray();

               if (binary.length > row)
               {
                  row = binary.length;
               }

               for (int k = 0; k < binary.length; k++)
               {
                  if (binary[k] == '1')
                  {
                     this.image.setPixel((BarcodeImage.MAX_HEIGHT - (binary.length - k) - 1), column, true);
                  }
                  else
                  {
                     this.image.setPixel((BarcodeImage.MAX_HEIGHT - (binary.length - k) - 1),  column,  false);
                  }
               }

               column++;
            }
         //}


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

   @Override
   public boolean translateImageToText()
   {
      // read each column and create binary number
      // convert binary number to decimal for ascii
      // convert ascii number to letter

      // char[] to hold each letter
      char[] tempChar = new char[this.text.length()];
      // String to hold the binary number generated
      String binNumber = "";
      // int for ascii number, will use Integer.parseInt(binaryString, 2)
      int ascii = 0;
      //int for char[] counter
      int counter = 0;

      for (int i = 1; i < this.getActualWidth(); i++)
      {
         for (int j = 0; j < this.getActualHeight(); j++)
         {
            //generate binary number string
            if (this.image.getPixel(i, j) == true)
            {
               binNumber = binNumber + '1';
            }
            else
            {
               binNumber = binNumber + '0';
            }

            //convert to ascii
            ascii = Integer.parseInt(binNumber, 2);

            //convert ascii to char and add to array
            tempChar[counter] = (char) ascii;

            //increment counter and reset others
            counter++;
            binNumber = "";
            ascii = 0;
         }
      }

      this.text = new String(tempChar);

      return true;
   }

   @Override
   public void displayTextToConsole()
   {
      System.out.println(this.text);

   }

   @Override
   public void displayImageToConsole()
   {
      for(int i = 0; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         for(int j = 0; j < BarcodeImage.MAX_WIDTH; j++)
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
      System.out.println("clean complete");
      /*int offset = BarcodeImage.MAX_HEIGHT - this.actualHeight;
      shiftImageDown(offset);
      offset = BarcodeImage.MAX_WIDTH - this.actualWidth;
      shiftImageLeft(offset);*/
      
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