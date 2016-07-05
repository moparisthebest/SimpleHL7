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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mopar on 6/30/16.
 */
public class MultithreadServer extends Server {
    public MultithreadServer(final ServerSocket ss, final MessageProcessor msgProcessor, final Encoding enc) {
        super(ss, msgProcessor, enc);
    }

    public MultithreadServer(final ServerSocket ss, final MessageProcessor msgProcessor) {
        super(ss, msgProcessor);
    }

    public MultithreadServer(final int port, final MessageProcessor msgProcessor, final Encoding enc) throws IOException {
        super(port, msgProcessor, enc);
    }

    public MultithreadServer(final int port, final MessageProcessor msgProcessor) throws IOException {
        super(port, msgProcessor);
    }

    @Override
    public void run() {
        boolean shouldShutdown = false;
        try {
            while (!ss.isClosed() && !(shouldShutdown = msgProcessor.shouldShutdown())) {
                final Socket s = ss.accept();
                new Thread(() -> {
                    try {
                        handleConnection(s);
                    } catch (final Throwable e) {
                        msgProcessor.handle(e, this);
                    } finally {
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
}
