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
      String[] testString = new String[]
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
      BarcodeImage test1 = new BarcodeImage();
      BarcodeImage test2 = new BarcodeImage(testString);

      test1.displayToConsole();
      test2.displayToConsole();

      String test = "Datamatrix test";
      DataMatrix test3 = new DataMatrix(test);

      test3.displayTextToConsole();
      test3.generateImageFromText();
      test3.displayImageToConsole();
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
            if(y == 0 || x == MAX_HEIGHT - 1)
            {
               this.setPixel(x, y, true);
            }
            else
            {
               this.setPixel(x, y, false);
            }
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
      this.text = text;
      generateImageFromText();
   }

   @Override
   public boolean scan(BarcodeImage bc)
   {
      try
      {
         image = (BarcodeImage)bc.clone();
         this.actualHeight = getActualHeight();
         this.actualWidth = getActualWidth();
         cleanImage();//not implemented yet
         return true;
      } catch (CloneNotSupportedException e) {
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
      if (this.text.length() < BarcodeImage.MAX_WIDTH) {
         String[] tempArray = this.text.split(" ");

         for (int i = 0; i < tempArray.length - 1; i++)
         {
            tempArray[i] = tempArray[i] + " ";
         }

         this.image = new BarcodeImage(tempArray);
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
      for(int i = BarcodeImage.MAX_HEIGHT - this.getActualHeight(); i < BarcodeImage.MAX_HEIGHT; i++)
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

            System.out.print("\n");
         }
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
      offset = BarcodeImage.MAX_HEIGHT - this.actualHeight;
      shiftImageDown(offset);
      offset = BarcodeImage.MAX_WIDTH - this.actualWidth;
      shiftImageLeft(offset);
   }

   private void shiftImageDown(int offset)
   {
      for(int x = actualWidth; x >=0; x--)
      {
         for(int y = actualHeight; y >= 0; y--)
         {
            if(y == 0 || x == MAX_HEIGHT - 1)
            {
               this.setPixel(x, y, true);
            }
            else
            {
               this.setPixel(x, y, false);
            }
         }
      }
   }

   private void shiftImageLeft(int offset)
   {

   }
}