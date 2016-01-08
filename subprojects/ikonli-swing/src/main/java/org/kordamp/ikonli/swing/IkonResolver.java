/*
 * Copyright 2015-2016 Andres Almiray. <aalmiray@yahoo.com>
 *
 * This file is part of Ikonli
 *
 * Ikonli is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ikonli is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ikonli. If not, see <http://www.gnu.org/licenses/>.
 *
 * ========================================================================
 *
 * This library is distributed under the terms of the GNU General Public
 * License with the following clarification and special exception:
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As an special exception, the copyright holders of this library give
 * you permission to use this library under the terms of the Apache
 * Software License v2 and forego the licensing terms of the GNU General
 * Public License if and only if the library is used as part of an
 * executable and/or application that makes use of the APIs of either the
 * Griffon Framework (https://github.com/griffon/griffon) or the
 * Basilisk Framework (https://github.com/basilisk-fw/basilisk).
 *
 * ========================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.ikonli.swing;

import org.kordamp.ikonli.IkonHandler;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author Andres Almiray
 */
public class IkonResolver {
    private static final IkonResolver INSTANCE;
    private static final Set<IkonHandler> HANDLERS = new LinkedHashSet<>();

    static {
        INSTANCE = new IkonResolver();

        ServiceLoader<IkonHandler> loader = ServiceLoader.load(IkonHandler.class);
        for (IkonHandler handler : loader) {
            HANDLERS.add(handler);

            try {
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(handler.getFontResourcePath());
                Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                stream.close();
                handler.setFont(font);
            } catch (FontFormatException | IOException ffe) {
                throw new IllegalStateException(ffe);
            }
        }
    }

    private IkonResolver() {

    }

    public static IkonResolver getInstance() {
        return INSTANCE;
    }

    public IkonHandler resolve(String value) {
        for (IkonHandler handler : HANDLERS) {
            if (handler.supports(value)) {
                return handler;
            }
        }
        return null;
    }
}
