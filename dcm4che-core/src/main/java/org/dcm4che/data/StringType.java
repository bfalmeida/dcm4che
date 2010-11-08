package org.dcm4che.data;

enum StringType {
    ASCII {
        @Override
        protected SpecificCharacterSet cs(SpecificCharacterSet cs) {
            return SpecificCharacterSet.DEFAULT;
        }
    },
    UI {
        @Override
        protected SpecificCharacterSet cs(SpecificCharacterSet cs) {
            return SpecificCharacterSet.DEFAULT;
        }

        @Override
        public String substring(String s, int beginIndex, int endIndex) {
            while (beginIndex < endIndex && s.charAt(endIndex - 1) <= ' ')
                endIndex--;
            return s.substring(beginIndex, endIndex);
        }
    },
    STRING,
    PN {
        @Override
        protected String codeExtensionDelimiters() {
            return "^=\\";
        }
    },
    TEXT {
        @Override
        protected String codeExtensionDelimiters() {
            return "\n\f\r";
        }

        @Override
        protected Object split(String s) {
            int endIndex = s.length();
            while (endIndex > 0 && s.charAt(endIndex - 1) == ' ')
                endIndex--;
            return s.substring(0, endIndex);
        }
    };

    public static String[] EMPTY_STRINGS = {};

    protected SpecificCharacterSet cs(SpecificCharacterSet cs) {
        return cs;
    }

    protected String codeExtensionDelimiters() {
        return "\\";
    }

    protected Object split(String s) {
        int count = 1;
        int delimPos = -1;
        while ((delimPos = s.indexOf('\\', delimPos+1)) >= 0)
            count++;

        if (count == 1)
            return substring(s, 0, s.length());

        String[] ss = new String[count];
        int delimPos2 = s.length();
        while (--count >= 0) {
            delimPos = s.lastIndexOf('\\', delimPos2-1);
            ss[count] = substring(s, delimPos+1, delimPos2);
            delimPos2 = delimPos;
        }
        return ss;
    }

    public byte[] toBytes(String s, SpecificCharacterSet cs) {
        return cs(cs).encode(s, codeExtensionDelimiters());
    }


    public byte[] toBytes(String[] ss, SpecificCharacterSet cs) {
        return toBytes(join(ss), cs);
    }

    protected String substring(String s, int beginIndex, int endIndex) {
        while (beginIndex < endIndex && s.charAt(beginIndex) == ' ')
            beginIndex++;
        while (beginIndex < endIndex && s.charAt(endIndex - 1) == ' ')
            endIndex--;
        return s.substring(beginIndex, endIndex);
    }

    private String join(String[] strings) {
        if (strings.length == 0)
            return null;
        
        if (strings.length == 1) {
            String s = strings[0];
            return s != null ? s : "";
        }
        int len = strings.length - 1;
        for (String s : strings)
            len += s != null ? s.length() : 0;

        StringBuilder sb = new StringBuilder(len);
        sb.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            sb.append('\\');
            String s = strings[i];
            if (s != null)
                sb.append(s);
        }
        return sb.toString();
    }

    public Object toStrings(byte[] b, SpecificCharacterSet cs) {
        return split(cs(cs).decode(b));
    }

    public String toString(byte[] b, SpecificCharacterSet cs) {
        return cs(cs).decode(b);
    }

}