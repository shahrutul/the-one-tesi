/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import config.M2MConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import protocol.transport.activities.Piece;

/**
 * @author armir
 *
 * This class has provides some utility functions needed by the application.
 */
public final class MixUtil {
    /**
     * This method is used for parsing the property file and returning an object
     * encapsulating it's content.
     *
     * @param byteContent byte[]: content of the property file
     */
    public static Properties formatProperties(byte[] byteContent) throws Exception {

        /**
         * Formating data
         */
        final Vector properties = new Vector();
        try{
            String toProcess = new String(byteContent);
            int next;
            while( (next = toProcess.indexOf("\n")) != -1) {
                String value = toProcess.substring(0,next-1);
                properties.addElement(value);
                toProcess = toProcess.substring(next+1, toProcess.length());
            }
            //last property
            properties.addElement(toProcess);
        }catch(Exception ex) {
            throw ex;
        }
        /**
         * building property obj
         */
        Properties struct = new Properties();
        try{
            for(int i =0; i < properties.size(); i++) {
                String keyValue = (String)properties.elementAt(i);
                int divisor = keyValue.indexOf("=");
                struct.setProperty(
                                    keyValue.substring(0, divisor),
                                    keyValue.substring(divisor+1, keyValue.length())
                                   );
            }
        }catch(Exception ex) {
            throw ex;
        }
        return struct;
    }
        
    /**
     * Converts the given integer value into 4 byte array.
     *
     * @param integer int: value to be converted
     *
     * @return byte[]: byte array rappresenting the integer value
     */
    public static byte[] intToByteArray (int integer) {
        
        byte[] toByte = new byte[4];
        toByte[0] =(byte)( integer >> 24 );
        toByte[1] =(byte)( (integer << 8) >> 24 );
        toByte[2] =(byte)( (integer << 16) >> 24 );
        toByte[3] =(byte)( (integer << 24) >> 24 );
        
        return toByte;
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }


    /** time format is: hh.mm*/
    public static boolean timeInRange(String inf, String sup) {

        int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minNow = Calendar.getInstance().get(Calendar.MINUTE);
        String timeNow = String.valueOf(hourNow)+"."+String.valueOf(minNow);

        if(Double.parseDouble(inf) <= Double.parseDouble(timeNow) &&
                Double.parseDouble(timeNow)<= Double.parseDouble(sup))
            return true;
        return false;
    }

    /** time format is: hh.mm */
    public static boolean intervalExpired(String sup) {

        int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minNow = Calendar.getInstance().get(Calendar.MINUTE);
        String timeNow = String.valueOf(hourNow)+"."+String.valueOf(minNow);
        if(Double.parseDouble(timeNow) > Double.parseDouble(sup))
            return true;
        return false;
    }    

    public static Object[] clean(Vector downloaded) throws Exception {

        /**
         * Order pieces in ascending order from their begining position
         */
        Piece[] pieces = new Piece[downloaded.size()];

        downloaded.copyInto(pieces);

        MixUtil.mergesort(pieces, 0, pieces.length);

        int i=0, j=1, length;
        /**
         * Eliminate duplicates and intersections
         */
        while(j < pieces.length) {

            Piece prec = (Piece)pieces[i];

            Piece succ = (Piece)pieces[j];

            if((length=intersect(prec, succ))!=-1 &&
                    j < pieces.length) {

                if( (prec.length() <= succ.length()) &&
                    (length >= prec.length())) {//prec contained in succ

                    pieces[i]=null;

                    i=j; j++;

                }else if( (succ.length() < prec.length()) &&
                        (length > succ.length())) {//succ contained in prec

                    pieces[j]=null;

                    j++;

                }else {

                    succ.setOffset(prec.getEndPosition()+1);

                    i=j; j++;
                }
            }else {

                i++; j++;
            }
        }
        return pieces;
    }

    private static int intersect(Piece prec, Piece succ) {
        if((prec.getInitPosition() <= succ.getInitPosition()) &&
                (succ.getInitPosition() <= prec.getEndPosition()))
            return prec.getEndPosition() -
                    succ.getInitPosition()
                     +1;
        return -1;
    }


   public static void mergesort(Object[] data, int first, int n)
   {
      int n1; // Size of the first half of the array
      int n2; // Size of the second half of the array
      if (n > 1)
      {
         // Compute sizes of the two halves
         n1 = n / 2;
         n2 = n - n1;

         mergesort(data, first, n1);      // Sort data[first] through data[first+n1-1]
         mergesort(data, first + n1, n2); // Sort data[first+n1] to the end

         // Merge the two sorted halves.
         if(data instanceof Piece[])
             merge((Piece[])data, first, n1, n2);
         else if(data instanceof Interval[])
                 merge((Interval[])data, first, n1, n2);
      }
   }

   private static void merge(Interval[] data, int first, int n1, int n2)
   // Precondition: data has at least n1 + n2 components starting at data[first]. The first
   // n1 elements (from data[first] to data[first + n1 – 1] are sorted from smallest
   // to largest, and the last n2 (from data[first + n1] to data[first + n1 + n2 - 1]) are also
   // sorted from smallest to largest.
   // Postcondition: Starting at data[first], n1 + n2 elements of data
   // have been rearranged to be sorted from smallest to largest.
   // Note: An OutOfMemoryError can be thrown if there is insufficient
   // memory for an array of n1+n2 ints.
   {
      Interval[ ] temp = new Interval[n1+n2]; // Allocate the temporary array
      int copied  = 0; // Number of elements copied from data to temp
      int copied1 = 0; // Number copied from the first half of data
      int copied2 = 0; // Number copied from the second half of data
      int i;           // Array index to copy from temp back into data

      // Merge elements, copying from two halves of data to the temporary array.
      while ((copied1 < n1) && (copied2 < n2))
      {
         if (data[first + copied1].getUpperEnd() < data[first + n1 + copied2].getLowerEnd())
            temp[copied++] = data[first + (copied1++)];
         else
            temp[copied++] = data[first + n1 + (copied2++)];
      }

      // Copy any remaining entries in the left and right subarrays.
      while (copied1 < n1)
         temp[copied++] = data[first + (copied1++)];
      while (copied2 < n2)
         temp[copied++] = data[first + n1 + (copied2++)];

      // Copy from temp back to the data array.
      for (i = 0; i < n1+n2; i++)
         data[first + i] = temp[i];
   }

   private static void merge(Piece[] data, int first, int n1, int n2)
   {
      Piece[ ] temp = new Piece[n1+n2]; // Allocate the temporary array
      int copied  = 0; // Number of elements copied from data to temp
      int copied1 = 0; // Number copied from the first half of data
      int copied2 = 0; // Number copied from the second half of data
      int i;           // Array index to copy from temp back into data

      // Merge elements, copying from two halves of data to the temporary array.
      while ((copied1 < n1) && (copied2 < n2))
      {
         if (data[first + copied1].getInitPosition() < data[first + n1 + copied2].getInitPosition())
            temp[copied++] = data[first + (copied1++)];
         else
            temp[copied++] = data[first + n1 + (copied2++)];
      }

      // Copy any remaining entries in the left and right subarrays.
      while (copied1 < n1)
         temp[copied++] = data[first + (copied1++)];
      while (copied2 < n2)
         temp[copied++] = data[first + n1 + (copied2++)];

      // Copy from temp back to the data array.
      for (i = 0; i < n1+n2; i++)
         data[first + i] = temp[i];
   }
   
   public static int[][] elaborateMap(int[] map) {

       int pointerPos = binarySearch(map, map[0], 1 , map.length);
       if(pointerPos!=-1) return new int[][]{new int[]{pointerPos}, map};
       int[] newMap = new int[map.length + 2];//new interval to be added
       int newMapPos=0, lower, upper;
       int pointerValue = map[0];
       newMap[newMapPos++] = map[0];

       for(int i=1; i < map.length; i+=2) {
           lower = map[i];
           upper = map[i+1];
           if(lower < pointerValue && upper > pointerValue) {
               newMap[newMapPos++] = lower;
               newMap[newMapPos++] = pointerValue-1;
               newMap[newMapPos++] = pointerValue;
               pointerPos = newMapPos-1;
               newMap[newMapPos++] = upper;
           }else {
               newMap[newMapPos++] = lower;
               newMap[newMapPos++] = upper;
           }
       }
       return new int[][]{new int[]{pointerPos},newMap};
   }

   private static int binarySearch(int[] values, int value, int low, int high) {
       if (high < low)
           return -1;
       int mid = low + ((high - low) / 2) ;
       if (values[mid] > value)
           return binarySearch(values, value, low, mid-1);
       else if (values[mid] < value)
           return binarySearch(values, value, mid+1, high);
       else
           return mid;
  }

   public static int mapSize(int[] downloadMap) {

       if(downloadMap.length==0) return 0;

       int valuePos;
       int sumInterval=0;
       int[][] mapApos = elaborateMap(downloadMap);
       valuePos = mapApos[0][0];
       downloadMap = mapApos[1];
           for(int i=valuePos; i<downloadMap.length; i+=2) {
               sumInterval+=(downloadMap[i+1]-downloadMap[i])+1;
           }       
       return sumInterval;
   }

    /**
     * Serializes file content in the outpustream given as input. The content serialized
     * is done by chunks in order not to overrrun the memory usage and network connection.
     * The outputStream is not closed as it is reused from callee again.
     */
    public static int writeOut(String persistedPath, OutputStream out) {

        FileConnection conn = null;
        InputStream in = null;
        int written = 0;
        try {
            try{
                conn = (FileConnection)Connector.open(persistedPath, Connector.READ_WRITE);
                if(!conn.exists()) {
                        throw new IOException("file" + persistedPath +"doesn't exist");
                }//else {
                    //conn.delete();
                    //conn.create();
                //}
            }catch(IOException ioex) {
                throw ioex;
            }

            //read file in and write out to outputStream
            byte[] readBuffer = new byte[512];
            int read;
            try {
                    //open inputstream to file
                    in = conn.openInputStream();

                    while((read=in.read(readBuffer))!=-1) {
                        //write out content
                        out.write(readBuffer, 0, read);

                        written+=read;
                    }
            }catch(IOException ioex) {
                throw ioex;
            }

        }catch(IOException ioex) {
        }catch(Throwable thr) {
        }finally{
            try {
                if(conn!=null)
                    conn.close();
                if(in!=null)
                    in.close();
            }catch(IOException ioex) {
            }
        }
        return written;
    }

    /**
     * Reads file content from the inputstream given as input. The content is read
     * by chunks in order not to overrrun the memory usage and network connection.
     * The inputStream is not closed as it is reused from callee again.
     */
    public static int readIn(String creationPath, InputStream in) {

        FileConnection conn = null;

        OutputStream out = null;

        int read =0;

        try {
            try{
                conn = (FileConnection)Connector.open(creationPath, Connector.READ_WRITE);

                if(!conn.exists()) {
                        MixUtil.createFile(creationPath, Connector.READ_WRITE);
                }else {
                    conn.delete();
                    conn.create();
                }

            }catch(IOException ioex) {
                throw ioex;
            }
            //read file in and write out to outputStream
            byte[] readBuffer = new byte[512];
            int read_;
            try {
                    //open outputstream to file
                    out = conn.openOutputStream();

                    while((read_=in.read(readBuffer))!=-1) {
                        //write out content
                        out.write(readBuffer, 0, read_);

                        read+=read_;
                    }
            }catch(IOException ioex) {
                throw ioex;
            }

        }catch(IOException ioex) {
        }catch(Throwable thr) {
        }finally{
            try {
                if (conn != null) {
                    conn.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
            }
        }
        return read;
    }

    
   /** creates entire file directory */
   public static FileConnection createFile(String absPathName, int RW_MODE) throws IOException {

       final String fsRoot = "file:///";
       int index = absPathName.indexOf(fsRoot);
       if(index!=-1)index+=fsRoot.length();
       String[] directory = splitPath(absPathName.substring(index, absPathName.length()));
       String actualDir = new String(fsRoot.getBytes());

       FileConnection conn = null;
       for(int i=0; i<directory.length; i++) {
           try {
               actualDir+=directory[i];
               if(actualDir.indexOf(".")==-1)actualDir+="/";
               conn = (FileConnection) Connector.open(actualDir, RW_MODE);
               if(conn!=null) {
                   if(!conn.exists()) {
                       if(actualDir.indexOf(".")!=-1) conn.create();
                       else conn.mkdir();
                   }
                   if(i!=directory.length-1)conn.close();
                }
            }catch (SecurityException sex) {
                return null;
            }catch (IOException ioex) {
                throw ioex;
            }
       }
       return conn;
    }

   private static String[] splitPath(String toSplit) {

        int i=0;
        Vector directories = new Vector();
        while(i!=-1) {
            i=toSplit.indexOf("/");
            if(i!=-1) {
                directories.addElement(new String(toSplit.substring(0,i)));
                toSplit = new String(toSplit.substring(i+1, toSplit.length()));
            }
        }
        if(toSplit.length()>0)directories.addElement(toSplit);
        String[] directory = new String[directories.size()];
        for(int j=0; j<directory.length; j++) {
            directory[j] = (String)directories.elementAt(j);
        }
       return directory;
   }


   public static void save(String filePath, byte[] byteContent) throws IOException {
       /**
        * Save object content !!
        */
       FileConnection conn = null;
       OutputStream out  = null;
       try{
           try{
               conn = (FileConnection)Connector.open(filePath, Connector.READ_WRITE);
               if(!conn.exists()) {
                   //create structure and file if it doesn't exist                   
                   conn = MixUtil.createFile(filePath, Connector.READ_WRITE);
               }else {
                   conn.delete();
                   conn.create();
               }
               out   = conn.openOutputStream();
           }catch(IOException ioex) {
               throw ioex;
           }catch(Throwable thr) {
           }
           try {
               out.write(byteContent);
           }catch(IOException ioex) {
               throw ioex;
           }
       }catch(IOException ioex) {
           throw ioex;
       }catch(Throwable thr) {
       }finally {
           if(out!=null)out.close();
           if(conn!=null)conn.close();
       }
   }


   public static byte[] loadFileAsByteStream(String filePath) throws Exception {

       FileConnection conn = null;
       int fileLength=0;
       conn = (FileConnection)Connector.open(filePath, Connector.READ);
       if(conn==null || !conn.exists()) throw new Exception("File " + filePath + " is missing !");
       try {
           fileLength = (int)conn.fileSize();
           conn.close();
       }catch(IOException ioex){
           throw ioex;
       }
       return loadFileAsByteStream(filePath,0 , fileLength);
   }

    public static byte[] loadFileAsByteStream(String filePath, int beginPosition, int endPosition) throws Exception {

       FileConnection conn = null;
       InputStream in  = null;
       ByteArrayOutputStream byteStream = null;
       byte[] content = null;
       try{
           try{
               conn = (FileConnection)Connector.open(filePath, Connector.READ);
               if(conn==null || !conn.exists()) throw new Exception("File " + filePath + " is missing !");
               in = conn.openInputStream();
           }catch(IOException ioex) {
               throw new IOException("loadContent::Error opening file from path " + filePath);
           }catch(SecurityException sex) {
           }

           int skipped = (int)in.skip(beginPosition);
           if(skipped!=beginPosition) throw new Exception("Couldn't skipp to beginPosition!!");
           byteStream = new ByteArrayOutputStream();
           byte[] buffer = new byte[512];
           int read;
           try {
               while(((read = in.read(buffer)) != -1) && skipped < endPosition) {
                   skipped+=read;
                   if(skipped>endPosition) read-=skipped-endPosition;
                   byteStream.write(buffer, 0, read);
               }
               content = byteStream.toByteArray();
           }catch(IOException ioex) {
               throw new IOException("loadContent::Error during file read");
           }catch(Throwable thr) {
           }
       }catch(Exception ex) {
            throw ex;
       }finally {
           if(conn!=null) 
               conn.close();
           if(in!=null)
               in.close();
           if(byteStream!=null)
               byteStream.close();
       }
       return content;
    }



   public static void deleteFile(String filePath) {
       FileConnection conn = null;
       try {
           conn = (FileConnection)Connector.open(filePath, Connector.READ_WRITE);
           conn.delete();
           conn.close();
       }catch(IOException ioex) {
       }catch(Throwable thr) {
       }
   }

   public static void deletePath(String path) {
       if(!path.endsWith("/")) path+="/";

       FileConnection conn = null;
       try {
           conn = (FileConnection)Connector.open(path, Connector.READ_WRITE);
           Enumeration files = conn.list();
           while(files.hasMoreElements()) {
               String toDelete = (String)files.nextElement();
               conn = (FileConnection)Connector.open(path+toDelete, Connector.READ_WRITE);
               conn.delete();
               conn.close();
           }
           //delete father directory
           conn = (FileConnection)Connector.open(path, Connector.READ_WRITE);
           conn.delete();
           conn.close();
       }catch(IOException ioex) {
       }catch(Throwable thr) {
       }
   }

   public static IntervalMap parseMap(String rawMap) {

       if(rawMap.length() == 0) return null;

       IntervalMap intervalMap = new IntervalMap();

       /**
         * Formating data
         */
        try{
            int next, lowerEnd, upperEnd;
            while( (next = rawMap.indexOf("\n")) != -1) {
                String interval = rawMap.substring(0,next-1);//(i,j)
                lowerEnd = Integer.parseInt(interval.substring(1, 2));
                upperEnd = Integer.parseInt(interval.substring(3, 4));
                intervalMap.orderedInsert(new Interval(lowerEnd, upperEnd));
                rawMap = rawMap.substring(next+1, rawMap.length());
            }
        }catch(Exception ex) {
            return null;
        }
        return intervalMap;
   }

   //key;key;key
   public static Vector parseSearchKeys(String searchKeys) {

        if(searchKeys.length() == 0) return null;

        Vector keys = new Vector();
       /**
         * Formating data
         */
        try{
            int next;

            while( (next = searchKeys.indexOf(";")) != -1) {
                keys.addElement(searchKeys.substring(0,next));
                searchKeys = searchKeys.substring(next+1, searchKeys.length());
            }
            /** add last key */
            keys.addElement(searchKeys);

        }catch(Exception ex) {
            return null;
        }

        return keys;
   }
   
   //file: fname;fHash;length | list: file*file* !
   public static Vector parseFileList(String fileList) {

       if(fileList.length() == 0) return null;

       Vector fileDescriptors = new Vector();

       int nextF, nextAtt;
       while( (nextF = fileList.indexOf("*")) != -1) {
           String fileDesc = fileList.substring(0,nextF);
           String[] fileAttribs = new String[3];
           int fileAtt = 0;
           while((nextAtt = fileDesc.indexOf(";")) != -1) {
                fileAttribs[fileAtt++] = fileDesc.substring(0,nextAtt);
                fileDesc = fileDesc.substring(nextAtt+1, fileDesc.length());
           }fileAttribs[fileAttribs.length-1] = fileDesc;

           fileDescriptors.addElement(fileAttribs);
           fileList = fileList.substring(nextF+1, fileList.length());
       }
       return fileDescriptors;
   }
   
   public static String shipFileList(Vector fileList) {
       
       StringBuffer shipList = new StringBuffer("");
       FileDescriptor fdescriptor = null;
       for(int i=0; i<fileList.size(); i++ ) {
            fdescriptor = (FileDescriptor)fileList.elementAt(i);
            shipList.append(fdescriptor.getFileName()+";");
            shipList.append(fdescriptor.getFileHash()+";");
            shipList.append(fdescriptor.getFileLength()+"*");
       }
       return shipList.toString();
   }
   
   private static MessageDigest md = null;


   public static String computeHash(FileConnection conn) throws IOException {

        //load byte quantity
        byte[] toHash = loadBytes(conn);
        byte[] digest = new byte[25];
        if(md==null)
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                return null;
            }
        //compute digest
        md.update(toHash, 0, toHash.length);
        try {
            md.digest(digest, 0, digest.length);
        } catch (DigestException ex) {
        }

        //convert byte digest to hex string
        StringBuffer hexString = new StringBuffer();
	for (int i=0;i<digest.length;i++) {
		hexString.append(Integer.toHexString(0xFF & digest[i]));
	}
        return hexString.toString();
    }

    public static String[] indexingTerms(String fileName) {

        int extIndex;
        fileName = ((extIndex=fileName.indexOf("."))!=-1)?
            fileName.substring(0, extIndex):fileName;

        String splitPoint = " ";
        String nextTerm = null;
        Vector temp = new Vector();
        int next;
        while((next=fileName.indexOf(splitPoint))!=-1) {
            nextTerm = fileName.substring(0,next);
            if(nextTerm.length() > 2 && !temp.contains(nextTerm))
                temp.addElement(nextTerm);
            fileName = fileName.substring(next+1, fileName.length());
        }
        if(fileName.length() > 2 && !temp.contains(fileName))
            temp.addElement(fileName);

        String[] terms = new String[temp.size()];
        temp.copyInto(terms);

        return terms;
    }

    private static byte[] loadBytes(FileConnection conn) throws IOException {

        if(conn==null || !conn.exists()) return null;

        InputStream in = conn.openDataInputStream();

        ByteArrayOutputStream byteStream =
                new ByteArrayOutputStream();
        int read, total=0;
        byte[] buffer = new byte[512];

        try {
            while((read=in.read(buffer))!=-1 &&
                    total < M2MConfig.BYTES_TO_HASH) {
                byteStream.write(buffer, 0, read);
                total+=read;
            }

        }catch(IOException ioex) {
            throw ioex;
        }finally {
            if(in != null)
                in.close();
        }

        return byteStream.toByteArray();
    }

    public static Vector listRoots() {

        Vector roots = new Vector();
        Enumeration enum_ = FileSystemRegistry.listRoots();
        while(enum_.hasMoreElements()) {
            roots.addElement("file:///" + enum_.nextElement().toString());
        }
        return roots;
    }

    /**
     * Lists all files contained under root directories
     */
    public static String[] listAllFiles() throws SecurityException {

        String file[] = null;
        Vector roots = listRoots();
        try {
            //don't add roots, they are not accessible
            file = searchContent(roots, false, true);
        }catch(SecurityException sex) {
            throw sex;
        }
        return file;
    }

    /**
     * Lists all files contained in specified directories
     */
    private static String[] searchContent(Vector searchPaths, boolean directory, boolean file) throws SecurityException
    {
        Vector baseFile = null;
        try {
              baseFile = expandDirs(searchPaths, directory, file);
        }catch(SecurityException sex) {
            throw sex;
        }
        String items[] = new String[baseFile.size()];
        for(int i = 0; i < baseFile.size(); i++)
            items[i] = (String)baseFile.elementAt(i);

        return items;
    }

    private static Vector expandDirs(Vector searchPaths, boolean directory, boolean file) throws SecurityException
    {
        Vector baseFile = new Vector();
        FileConnection conn = null;
        try
        {
            for(int i = 0; i < searchPaths.size(); i++)
            {
                try
                {
                    {
                        conn = (FileConnection)Connector.open(searchPaths.elementAt(i).toString(), Connector.READ);
                    }
                }catch(SecurityException sex) {
                    throw sex;
                }catch(IOException ex)
                {
                }
                if(conn != null)
                    if(conn.isDirectory())
                    {
                        if(directory)baseFile.addElement(conn.getURL());
                        expandDir(conn, baseFile, directory, file);
                    } else
                    {
                        if(file)baseFile.addElement(conn.getURL());
                    }
                conn = null;
            }

        }catch(SecurityException sex) {
            throw sex;
        }catch(Exception ex)
        {
        }
        return baseFile;
    }

    private static void expandDir(FileConnection conn, Vector baseFile,
                                                       boolean directory,
                                                       boolean file)
                                                       throws SecurityException
    {
        String currentDir = conn.getURL();
        Enumeration enum_ = null;
        try
        {
            try
            {
                enum_ = conn.list();
            }catch(SecurityException sex) {
                throw sex;

            }catch(IOException ex)
            {
            }

            while(enum_ != null && enum_.hasMoreElements())
            {
                String fName = (String)enum_.nextElement();
                try
                {
                    conn = (FileConnection)Connector.open(currentDir + fName, 1);
                }
                catch(IOException ex)
                {
                }
                if(conn!=null)
                    if(conn.isDirectory()) {
                        if(directory)baseFile.addElement(conn.getURL());
                        expandDir(conn, baseFile, directory, file);
                    }
                    else
                    {
                        if(file)baseFile.addElement(conn.getURL());
                    }
            }
        }catch(SecurityException sex) {
               throw sex;
        }catch(Exception ex){
        }
    }
}
