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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

/**
 * Created by mopar on 6/30/16.
 */
public class Server implements Runnable, Closeable {

    protected final ServerSocket ss;
    protected final Encoding enc;
    protected final MessageProcessor msgProcessor;

    public Server(final ServerSocket ss, final MessageProcessor msgProcessor, final Encoding enc) {
        this.ss = ss;
        this.msgProcessor = msgProcessor;
        this.enc = enc;
    }

    public Server(final ServerSocket ss, final MessageProcessor msgProcessor) {
        this(ss, msgProcessor, Message.DEFAULT_ENCODING);
    }

    public Server(final int port, final int soTimeout, final MessageProcessor msgProcessor, final Encoding enc) throws IOException {
        this(new ServerSocket(port), msgProcessor, enc);
        if(soTimeout >= 0)
            this.ss.setSoTimeout(soTimeout);
    }

    public Server(final int port, final MessageProcessor msgProcessor, final Encoding enc) throws IOException {
        this(port, -1, msgProcessor, enc);
    }

    public Server(final int port, final int soTimeout, final MessageProcessor msgProcessor) throws IOException {
        this(port, soTimeout, msgProcessor, Message.DEFAULT_ENCODING);
    }

    public Server(final int port, final MessageProcessor msgProcessor) throws IOException {
        this(port, msgProcessor, Message.DEFAULT_ENCODING);
    }

    @Override
    public void run() {
        boolean shouldShutdown = false;
        try {
            while (!ss.isClosed() && !(shouldShutdown = msgProcessor.shouldShutdown())) {
                try (Socket s = ss.accept()) {
                    handleConnection(s);
                } catch (final Throwable e) {
                    msgProcessor.handle(e, this);
                }
            }
        } catch (final Throwable e) {
            try {
                this.close();
            } catch (Throwable e2) {
                // ignore
            }
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
        if(shouldShutdown)
            try {
                this.close();
            } catch (Throwable e2) {
                // ignore
            }
    }

    protected void handleConnection(final Socket s) throws IOException, ParseException {
        if (msgProcessor.allowConnection(s))
            try (InputStream is = s.getInputStream();
                 OutputStream os = s.getOutputStream()) {
                Message in;
                while((in = Message.readFrom(is, enc)) != null) {
                    final Message out = msgProcessor.process(in);
                    if (out != null)
                        out.writeTo(os, enc);
                }
            }
    }

    @Override
    public void close() throws IOException {
        ss.close();
    }
}
