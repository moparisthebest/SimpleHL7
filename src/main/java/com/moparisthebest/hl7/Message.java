/*
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * Copyright (c) 2016 Travis Burtrum.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */

package com.moparisthebest.hl7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Message {

    public static final Encoding DEFAULT_ENCODING = new Encoding('\r', '~', '|', '^', '&', '\\'); // http://healthstandards.com/blog/2006/11/02/hl7-escape-sequences/
    public static final Encoding NEWLINE_ENCODING = new Encoding('\n', '~', '|', '^', '&', '\\');

    private final List<Line> lines = new ArrayList<>();

    public Message() {
        line("MSH");
    }

    public Message(final String sendingApplication, final String sendingFacility, final String receivingApplication, final String receivingFacility, final String messageCode, final String messageTriggerEvent, final String messageControlId, final String processingId, final String version) {
        final Line msh = line("MSH");
        msh.field(3).value(sendingApplication);
        msh.field(4).value(sendingFacility);
        msh.field(5).value(receivingApplication);
        msh.field(6).value(receivingFacility);
        msh.field(7).date(Instant.now());

        final Field messageType = msh.field(9);
        messageType.component(1).value(messageCode);
        messageType.component(2).value(messageTriggerEvent);
        messageType.component(3).value(messageCode + "_" + messageTriggerEvent);

        msh.field(10).value(messageControlId);
        msh.field(11).value(processingId);
        msh.field(12).value(version);
    }

    public Message(final String s) throws ParseException {
        final int mshIdx = s.indexOf("MSH");
        if(mshIdx == -1 || s.length() < (mshIdx + 8))
            throw new ParseException("HL7 messages must start with an MSH segment", 0);

        // detect segmentDelimiter
        char segmentDelimiter = DEFAULT_ENCODING.segmentDelimiter;
        final char fieldDelimiter = s.charAt(mshIdx + 3);
        byte fieldDelimiterCount = 0;
        // 11 fields in is the version, next non-number or . is segmentDelimiter
        for(int i = mshIdx; i < s.length(); ++i) {
            if(fieldDelimiterCount < 11) {
                if(s.charAt(i) == fieldDelimiter)
                    ++fieldDelimiterCount;
            } else {
                final char c = s.charAt(i);
                if(c != '.' && !Character.isDigit(c)) {
                    segmentDelimiter = c;
                    break;
                }
            }
        }

        final Encoding enc = new Encoding(segmentDelimiter, s.charAt(mshIdx + 5), fieldDelimiter, s.charAt(mshIdx + 4), s.charAt(mshIdx + 7), s.charAt(mshIdx + 6));
        for(final String line : enc.splitSegment("MSH" + fieldDelimiter + s.substring(mshIdx + 8)))
            if(!line.isEmpty())
                lines.add(new Line(line, enc));
        // shift MSH one to account for brain-dead 1st field while setting encoding
        final Line msh = this.lines.get(0);
        msh.field(2); // expand array sufficiently
        msh.subComponents.add(1, enc.msh1);
        msh.subComponents.set(2, enc);
    }

    public String encode() {
        return this.encode(DEFAULT_ENCODING);
    }

    public String encode(final Encoding enc) {
        // set encoding
        final Line msh = this.lines.get(0);
        msh.field(2); // expand array sufficiently
        msh.subComponents.set(1, enc.msh1);
        msh.subComponents.set(2, enc);

        final StringBuilder sb = new StringBuilder();
        for(final Line line : lines)
            line.encode(sb, enc);
        if(!lines.isEmpty()){
            int index = 1;
            while (index > 0 && sb.charAt(index = sb.length() - 1) == enc.segmentDelimiter)
                sb.setLength(index);
        }
        return sb.toString();
    }

    public Line optionalLine(final String type) {
        for(final Line line : lines)
            if(line.field(0).equals(type))
                return line;
        return null;
    }

    public Line line(final String type) {
        Line line = optionalLine(type);
        if(line == null)
            lines.add(line = new Line(type));
        return line;
    }

    public Line line(final int index) {
        return lines.get(index);
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Line> lines(final String type) {
        return lines.stream().filter(l -> l.field(0).equals(type)).collect(Collectors.toList());
    }

    public Line add(final String line) {
        final Line ret = new Line(line);
        lines.add(ret);
        return ret;
    }

    public Message add(final Line line) {
        lines.add(line);
        return this;
    }

    public Message writeAndRead(final Socket sock, final Encoding enc) throws IOException, ParseException {
        return this.writeAndRead(sock.getOutputStream(), sock.getInputStream(), enc);
    }

    public Message writeAndRead(final OutputStream os, final InputStream is, final Encoding enc) throws IOException, ParseException {
        this.writeTo(os, enc);
        os.flush();
        return readFrom(is, enc);
    }

    public void writeTo(final OutputStream os, final Encoding enc) throws IOException {
        os.write(enc.verticalTab);
        // todo: this can be made dramatically faster by writing to this directly instead of a StringBuilder
        os.write(this.encode(enc).getBytes(StandardCharsets.US_ASCII));
        os.write(enc.fileSeperator);
        os.write(enc.segmentDelimiter);
    }

    public static Message readFrom(final InputStream is, final Encoding enc) throws IOException, ParseException {
        final StringBuilder sb = new StringBuilder();
        for (int i = -1; (i = is.read()) != -1; ) {
            //System.out.println("i: "+i);
            final char c = (char) i;
            //System.out.println("c: "+c);
            if (c == enc.fileSeperator) { // end of line/message
                // next char should be segmentDelimiter
                i = is.read();
                if (((char) i) != enc.segmentDelimiter)
                    throw new ParseException("HL7 messages have a segmentDelimiter directly following a fileSeperator", sb.length());
                return new Message(sb.toString());
            } else {
                sb.append(c);
            }
        }
        return null; // no complete message received
    }

    public Message writeAndRead(final Socket sock) throws IOException, ParseException {
        return this.writeAndRead(sock, DEFAULT_ENCODING);
    }

    public Message writeAndRead(final OutputStream os, final InputStream is) throws IOException, ParseException {
        return this.writeAndRead(os, is, DEFAULT_ENCODING);
    }

    public void writeTo(final OutputStream os) throws IOException {
        this.writeTo(os, DEFAULT_ENCODING);
    }

    public static Message readFrom(final InputStream is) throws IOException, ParseException {
        return readFrom(is, DEFAULT_ENCODING);
    }

    @Override
    public String toString() {
        return "Message" + lines;
    }
}
