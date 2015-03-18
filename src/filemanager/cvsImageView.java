package filemanager; // ���������

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.game.*;
import java.io.*;
import com.vmx.*;

/**
 * ����� - ����������� ��������
 */
public class cvsImageView extends gkcCanvas
{
    private Displayable parent;
    private javax.microedition.lcdui.Image currentImage = null;
    private boolean enableUI = true;
    private boolean rotate;
    private boolean scaled;
    private int pictureWidth, pictureHeight;
    int w, h;
    private int curposx, curposy;
    private Sprite picture;
    int selectedIndex;
    String currentPictureFile;
    Font nameFont;
    /**
     * �����������
     */
    cvsImageView ()
    {
        setFullScreenMode (true);
        w = getWidth ();
        h = getHeight ();
        nameFont = Font.getFont (Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    }
    /**
     * ���������� ������� �������� ��������
     */
    protected void predisplay ()
    {
        serviceRepaints ();
        selectedIndex = main.FileSelect.scrSel;
        currentImage = null;
        pictureWidth = -1;
        pictureHeight = -1;
        curposx = 0;
        curposy = 0;
        scaled = false;
        rotate = false;
    }
    /**
     * ����������� ������� �������� ��������
     */
    protected void postdisplay ()
    {
        if (currentImage != null)
        {
            pictureWidth = currentImage.getWidth ();
            pictureHeight = currentImage.getHeight ();
            picture = new Sprite (currentImage);
            if (currentImage.getWidth() > currentImage.getHeight())
            {
                rotate = true;
                swapHW ();
            }
            placeImageToCenter ();
        }
    }
    
    public void displayImageFromStream (InputStream is, Displayable parent)
    {
        predisplay ();
        this.parent = parent;
        currentPictureFile = main.currentPath + main.FileSelect.files[selectedIndex]; //!!!
        try
        {
            currentImage = Image.createImage (is);
        } catch (Exception x) { currentImage = null; }
        try
        {
            is.close ();
            postdisplay ();
        } catch (Exception x) { currentImage = null; }
        repaint ();
    }
    
    public void displayImage (String imgName, Displayable parent)
    {
        predisplay ();
        this.parent = parent;
        currentPictureFile = imgName;
        // ������ �������� � ������ ������, ����� �� ������, ����� �� ������, ���� �� �������
        if ((currentImage = readImageFromFile (imgName, false)) == null)
        {
            if ((currentImage = readImageFromFile (imgName, 0, h)) == null)
                currentImage = readImageFromFile (imgName, h, 0);
            if (currentImage != null)
                scaled = true;
        }
        if (currentImage != null)
        {
            boolean hbig = currentImage.getHeight () > h;
            boolean wbig = currentImage.getWidth () > w;
            if (hbig || wbig)
            {
                scaled = true;
                // ���� � ������ ������, � ������ ����, ����� ��������,
                // �� ������ ��� ������ ��������� � �����
                if (hbig && wbig)
                {
                    currentImage = readImageFromFile (imgName, 0, h);
                    if (currentImage.getHeight()*w/h < currentImage.getWidth())
                    {
                        if (currentImage.getWidth () > currentImage.getHeight ())
                            currentImage = readImageFromFile (imgName, h, 0);
                        else currentImage = readImageFromFile (imgName, w, 0);
                    }
                }
                // ���� ������ ������ - �� �� ������
                else if (hbig)
                    currentImage = readImageFromFile (imgName, 0, h);
                // ���� ������ ������ - �� �������, � �� ������� �� ��� ����� ������������?
                // ���� �������, �� ����������� �� "������", �� "������" ��� ������ ������ ������
                else if (wbig)
                {
                    if (currentImage.getWidth () > currentImage.getHeight ())
                        currentImage = readImageFromFile (imgName, h, 0);
                    else currentImage = readImageFromFile (imgName, w, 0);
                }
            }
            postdisplay();
        }
        repaint ();
    }
    /**
     * ������� ������� ���������� Siemens
     *
     * @param imgName String
     * @param h int - ������
     * @param w int - ������
     * @return Image
     */
    public final javax.microedition.lcdui.Image readImageFromFile (String imgName, int w, int h)
    {
        javax.microedition.lcdui.Image image = null;
        try
        {
            image = com.siemens.mp.lcdui.Image.createImageFromFile (imgName, w, h);
        } catch (Exception e) { image = null; }
        return image;
    }
    /**
     * ������� ������� ���������� Siemens
     *
     * @param imgName String
     * @param Scale boolean
     * @return Image
     */
    public final javax.microedition.lcdui.Image readImageFromFile (String imgName, boolean Scale)
    {
        javax.microedition.lcdui.Image image = null;
        try
        {
            image = com.siemens.mp.lcdui.Image.createImageFromFile (imgName, Scale);
        } catch (Exception e) { image = null; }
        return image;
    }
    /**
     * ������� ���������
     */
    protected void paint (Graphics g)
    {
        // ���
        if (currentImage != null)
        {
            g.setColor (0x000000);
            g.fillRect (0, 0, w, h);
            if (rotate)
                picture.setTransform (Sprite.TRANS_ROT270);
            placeImageToCenter ();
            picture.setPosition (curposx, curposy);
            picture.paint (g);
        }
        else // ��� �����������
            g.drawRegion (images.waitAnim, 0, 0, 32, 32, Sprite.TRANS_NONE, w/2-16, h/2-16, Graphics.LEFT | Graphics.TOP);
        if (enableUI)
        {
            g.drawRegion (images.playerUI, 0, 146,  w/2, 30,  0,  0, h - 30, Graphics.TOP | Graphics.LEFT);
            g.drawRegion (images.playerUI, 132 - w/2, 146,  w/2, 30,  0,  w - w/2, h - 30, Graphics.TOP | Graphics.LEFT);
            String tmp = main.FileSelect.files[selectedIndex];
            g.setFont (nameFont);
            g.setColor (0x800000);
            g.drawString (tmp, w/2, h - 26, g.TOP | g.HCENTER);
            tmp = pictureWidth + " x " + pictureHeight + (scaled ? Locale.Strings[Locale.IMAGEVIEW_SCALED] : "");
            g.setColor (0x000080);
            g.drawString (tmp, w/2, h - 13, g.TOP | g.HCENTER);
        }
    }
    /** ����������� ��������� ������ �������� ���� �������� */
    private void placeImageToCenter ()
    {
        curposx = getWidth()/2 - pictureWidth/2;
        curposy = getHeight()/2 - pictureHeight/2;
    }
    /** �������� ������� pictureHeight � pictureWidth */
    private void swapHW ()
    {
        int r = pictureHeight;
        pictureHeight = pictureWidth;
        pictureWidth = r;
    }
    /** ���������� ������� ������ */
    protected void keyPressed (int keyCode)
    {
        if (keyCode == KEY_POUND)
        {
            enableUI = !enableUI;
            repaint ();
        }
        // ��������� ��������
        else if (keyCode == KEY_DOWN || keyCode == KEY_NUM8)
            nextPicture ();
        // �������� ��������
        else if (keyCode == KEY_UP || keyCode == KEY_NUM2)
            prevPicture ();
        // �����
        else if (keyCode == KEY_CANCEL || keyCode == KEY_RSK)
            main.dsp.setCurrent (parent);
    }
    /**
     * ��������� ��������
     */
    private void nextPicture ()
    {
        currentImage = null;
        repaint ();
        main.FileSelect.select (selectedIndex = main.FileSelect.getNextOfType (selectedIndex, filesystem.TYPE_PICTURE));
        currentPictureFile = main.currentPath + main.FileSelect.files[selectedIndex];
        main.currentFile = main.FileSelect.files[selectedIndex];
        main.FileSelect.selectFile ();
    }
    /**
     * ���������� ��������
     */
    private void prevPicture ()
    {
        currentImage = null;
        repaint ();
        main.FileSelect.select (selectedIndex = main.FileSelect.getPrevOfType (selectedIndex, filesystem.TYPE_PICTURE));
        currentPictureFile = main.currentPath + main.FileSelect.files[selectedIndex];
        main.currentFile = main.FileSelect.files[selectedIndex];
        main.FileSelect.selectFile ();
    }
}
