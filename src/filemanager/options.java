package filemanager;

import javax.microedition.rms.*;
import java.io.*;
import java.util.Vector;

public class options
{
    // �������� �������� RMS
    private static final String optionsName = "SieFM";
    private static final String favoritesName = "SieFM_favorites";
    // ��������� ��� ��������
    private static RecordStore optionsStore;
    private static RecordStore favoritesStore;
    // ���������
    public static boolean firstTime = true; // ������ ��� �������
    public static boolean showHidden = false; // ���������� �������
    public static int volume = 100; // ��������� ����� �������
    public static boolean muted = false; // �������� ���� � ������������?
    public static boolean quickSplash = false; // ������� �������� ��������
    public static boolean showDisk3 = false; // ���������� ���� 3:/
    public static boolean openNotSupported = false; // ���������� ����������������
    public static boolean noEffects = false; // �� ���������� ������ "���������"
    public static String language = "en";
    // ���������
    protected static Vector favorites = new Vector ();
    /**
     * ������ �����������
     */
    public options ()
    {
    }
    /**
     * ���������� ��������
     */
    public static void saveOptions ()
    {
        if (optionsStore != null)
        {
            byte [] options = null;
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                DataOutputStream dos = new DataOutputStream (baos);
                dos.writeBoolean (firstTime);
                dos.writeBoolean (showHidden);
                dos.writeByte ((byte)volume);
                dos.writeBoolean (muted);
                dos.writeBoolean (quickSplash);
                dos.writeBoolean (showDisk3);
                dos.writeBoolean (openNotSupported);
                dos.writeBoolean (noEffects);
                dos.writeUTF (language);
                for (int i = 0; i < keyConfig.keyConfig.length; i++)
                    dos.writeInt (keyConfig.keyConfig[i]);
                dos.flush ();
                options = baos.toByteArray ();
                dos.close ();
                optionsStore.setRecord (1, options, 0, options.length);
            }
            catch (InvalidRecordIDException ridex)
            {
                // ������ #1 �� ����������, ������� �����
                try { optionsStore.addRecord (options, 0, options.length); }
                catch (RecordStoreException ex)
                {
                    //System.out.println ("Could not add options record");
                }
            }
            catch (Exception ex)
            {
                //System.out.println ("Could not save options");
            }
        }
        if (optionsStore != null)
        {
            try
            {
                optionsStore.closeRecordStore ();
                optionsStore = null;
            }
            catch (RecordStoreException ex)
            {
                //System.out.println ("Could not close options storage");
            }
        }
    }
    /**
     * ������������ ���������
     */
    public static void restoreOptions ()
    {
        try
        {
            optionsStore = RecordStore.openRecordStore (optionsName, true);
        }
        catch (RecordStoreException ex)
        {
            optionsStore = null;
            //System.out.println ("* optionsStore not created *");
        }
        if (optionsStore != null)
        {
            try
            {
                DataInputStream dis = new DataInputStream (new ByteArrayInputStream (optionsStore.getRecord (1)));
                firstTime = dis.readBoolean (); // ������ ��� �������
                showHidden = dis.readBoolean (); // ���������� �������
                volume = dis.readByte (); if (volume < 0) volume = -volume; // ��������� ����� �������
                muted = dis.readBoolean (); // �������� ���� � ������������?
                quickSplash = dis.readBoolean (); // ������� �������� ��������
                showDisk3 = dis.readBoolean (); // ���������� ���� 3:/
                openNotSupported = dis.readBoolean (); // ���������� ����������������
                noEffects = dis.readBoolean (); // ��� "���������"
                language = dis.readUTF ();
                for (int i = 0; i < keyConfig.keyConfig.length; i++)
                    keyConfig.keyConfig[i] = dis.readInt ();
                dis.close ();
            } catch (Exception ex) {}
        }
    }
    /**
     * ���������� ����������
     */
    public static void saveFavorites ()
    {
        try
        {
            RecordStore.deleteRecordStore (favoritesName);
        } catch (RecordStoreException e) {}
        try
        {
            favoritesStore = RecordStore.openRecordStore (favoritesName, true);
            for (int i = 0; i < favorites.size(); i++)
            {
                try
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                    DataOutputStream dos = new DataOutputStream (baos);
                    dos.writeUTF ((String)favorites.elementAt(i));
                    byte b[] = baos.toByteArray ();
                    favoritesStore.addRecord (b, 0, b.length);
                } catch (Exception e) {}
            }
            favoritesStore.closeRecordStore ();
        }
        catch (Exception e) {}
    }
    /**
     * ��������� ������� ����� - ����������
     */
    public static String[] getFavorites ()
    {
        String [] fav = new String [favorites.size()];
        favorites.copyInto (fav);
        return fav;
    }
    /**
     * ���������� � ���������
     */
    public static void addFavorite (String nf)
    {
        favorites.addElement (nf);
    }
    /**
     * �������� �� ����������
     */
    public static void deleteFavorite (String ff)
    {
        favorites.removeElement (ff);
    }
    /**
     * ������ ����� �� ���������
     */
    public static void loadFavorites ()
    {
        favorites.removeAllElements ();
        int recordsNum = 0;
        try
        {
            favoritesStore = RecordStore.openRecordStore (favoritesName, true);
            try
            {
                for (RecordEnumeration enumX = favoritesStore.enumerateRecords (null, null, true); enumX.hasNextElement (); )
                {
                    int recId = enumX.nextRecordId ();
                    ByteArrayInputStream bais = new ByteArrayInputStream (favoritesStore.getRecord (recId));
                    DataInputStream dis = new DataInputStream (bais);
                    String in = dis.readUTF ();
                    favorites.addElement (in);
                }
            } catch (Exception e) {}
            favoritesStore.closeRecordStore ();
        } catch (RecordStoreNotFoundException e) {}
        catch (RecordStoreException e) {}
    }
}
