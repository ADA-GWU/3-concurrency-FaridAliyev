## Prerequisites

Before you run the code, ensure you have met the following requirements:

- Have Java installed on your system
- The java code is written using `jdk version 17`. Use the same version to prevent potential errors that may arise due to version mismatch.

## Project Structure

Here's an overview of how this project is structured:

- **`/src/main/java`**: This directory contains the source code of the project.
  - `Program.java`: The main part of the project that is responsible for processing the images based on the provided input.
  
- **`/src/main/images`**: This directory contains the image files that may be used as input to the program. The resulting images after processing are also stored in this folder. Some input and output image files are already included for you to use as input or just to check the results. 

## How to Test the Code

To test the code on your local machine, follow these steps:

1. **Compile the java code**:
   Navigate to the root folder (the folder that contains the `src` folder) and run this command:
   ```sh
   javac src/main/java/Program.java
   ```
2. **Run the program**:
   In the root folder, run the command of this form:
   ```sh
   java -cp src/main/java src/main/java/Program.java [arg1] [arg2] [arg3]
   ```
   The program expects 3 arguments from the user:
   - The first argument is the name of the image file (without the extension, e.g. *Mona_Lisa*) that is located in the `images` folder.
   - The second argument is the size of the square for the averaging. (integer)
   - The third argument is the processing mode, either ***S*** for single thread mode, or ***M*** for multithread mode. ***Note***: The processing mode argument is case-insensitive, it means both ***S*** and ***s*** are accepted.
  
  Here is an example of a working command:
  ```sh
   java -cp src/main/java src/main/java/Program.java Mona_Lisa 20 s
   ```
  After running this command a pop-up window will open where you will see your input image get processed and updated live. ***Warning***: Do not close this window until the process is finished. The resulting image file will also be saved to the `images` folder with the file name of ***_result*** appended to the original file name if you want to check the result later.


***HAVE FUN TESTING THE CODE :)***
