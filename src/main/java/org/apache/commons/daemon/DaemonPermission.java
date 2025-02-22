/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.commons.daemon;

import java.security.Permission;
import java.util.StringTokenizer;

/**
 * Represents the permissions to control and query the status of
 * a {@code Daemon}. A {@code DaemonPermission} consists of a
 * target name and a list of actions associated with it.
 * <p>
 * In this specification version the only available target name for this
 * permission is &quot;control&quot;, but further releases may add more target
 * names to fine-tune the access that needs to be granted to the caller.
 * </p>
 * <p>
 * Actions are defined by a string of comma-separated values, as shown in the
 * table below. The empty string implies no permission at all, while the
 * special &quot;*&quot; value implies all permissions for the given
 * name:
 * </p>
 * <table border="1">
 *  <caption>Supported Actions</caption>
 *  <tr>
 *   <th>Target&quot;Name</th>
 *   <th>Action</th>
 *   <th>Description</th>
 *  </tr>
 *  <tr>
 *   <td rowspan="5">&quot;control&quot;</td>
 *   <td>&quot;start&quot;</td>
 *   <td>
 *    The permission to call the {@code start()} method in an instance
 *    of a {@code DaemonController} interface.
 *   </td>
 *  </tr>
 *  <tr>
 *   <td>&quot;stop&quot;</td>
 *   <td>
 *    The permission to call the {@code stop()} method in an instance
 *    of a {@code DaemonController} interface.
 *   </td>
 *  </tr>
 *  <tr>
 *   <td>&quot;shutdown&quot;</td>
 *   <td>
 *    The permission to call the {@code shutdown()} method in an instance
 *    of a {@code DaemonController} interface.
 *   </td>
 *  </tr>
 *  <tr>
 *   <td>&quot;reload&quot;</td>
 *   <td>
 *    The permission to call the {@code reload()} method in an instance
 *    of a {@code DaemonController} interface.
 *   </td>
 *  </tr>
 *  <tr>
 *   <td>&quot;*&quot;</td>
 *   <td>
 *    The special wildcard action implies all above-mentioned action. This is
 *    equal to construct a permission with the &quot;start, stop, shutdown,
 *    reload&quot; list of actions.
 *   </td>
 *  </tr>
 * </table>
 */
public final class DaemonPermission extends Permission
{

    /* ====================================================================
     * Constants.
     */

    private static final long serialVersionUID = -8682149075879731987L;

    /**
     * The target name when associated with control actions
     * (&quot;control&quot;).
     */
    protected static final String CONTROL = "control";

    /**
     * The target type when associated with control actions.
     */
    protected static final int TYPE_CONTROL = 1;

    /**
     * The action name associated with the permission to call the
     * {@code DaemonController.start()} method.
     */
    protected static final String CONTROL_START = "start";

    /**
     * The action name associated with the permission to call the
     * {@code DaemonController.stop()} method.
     */
    protected static final String CONTROL_STOP = "stop";

    /**
     * The action name associated with the permission to call the
     * {@code DaemonController.shutdown()} method.
     */
    protected static final String CONTROL_SHUTDOWN = "shutdown";

    /**
     * The action name associated with the permission to call the
     * {@code DaemonController.reload()} method.
     */
    protected static final String CONTROL_RELOAD = "reload";

    /**
     * The action mask associated with the permission to call the
     * {@code DaemonController.start()} method.
     */
    protected static final int MASK_CONTROL_START = 0x01;

    /**
     * The action mask associated with the permission to call the
     * {@code DaemonController.stop()} method.
     */
    protected static final int MASK_CONTROL_STOP = 0x02;

    /**
     * The action mask associated with the permission to call the
     * {@code DaemonController.shutdown()} method.
     */
    protected static final int MASK_CONTROL_SHUTDOWN = 0x04;

    /**
     * The action mask associated with the permission to call the
     * {@code DaemonController.reload()} method.
     */
    protected static final int MASK_CONTROL_RELOAD = 0x08;

    /**
     * The &quot;wildcard&quot; action implying all actions for the given
     * target name.
     */
    protected static final String WILDCARD = "*";

    /* ====================================================================
     * Instance variables
     */

    /** The type of this permission object. */
    private transient int type;
    /** The permission mask associated with this permission object. */
    private transient int mask;
    /** The String representation of this permission object. */
    private transient String desc;

    /* ====================================================================
     * Constructors
     */

    /**
     * Creates a new {@code DaemonPermission} instance with a specified
     * permission name.
     * <p>
     * This constructor will create a new {@code DaemonPermission}
     * instance that <b>will not</b> grant any permission to the caller.
     *
     * @param target The target name of this permission.
     * @throws IllegalArgumentException If the specified target name is not
     *                supported.
     */
    public DaemonPermission(final String target)
        throws IllegalArgumentException
    {
        // Set up the target name of this permission object.
        super(target);

        // Check if the permission target name was specified
        if (target == null) {
            throw new IllegalArgumentException("Null permission name");
        }

        // Check if this is a "control" permission and set up accordingly.
        if (CONTROL.equalsIgnoreCase(target)) {
            type = TYPE_CONTROL;
            return;
        }

        // If we got here, we have an invalid permission name.
        throw new IllegalArgumentException("Invalid permission name \"" +
                                           target + "\" specified");
    }

    /**
     * Creates a new {@code DaemonPermission} instance with a specified
     * permission name and a specified list of actions.
     *
     * @param target The target name of this permission.
     * @param actions The list of actions permitted by this permission.
     * @throws IllegalArgumentException If the specified target name is not
     *                supported, or the specified list of actions includes an
     *                invalid value.
     */
    public DaemonPermission(final String target, final String actions)
        throws IllegalArgumentException
    {
        // Setup this instance's target name.
        this(target);

        // Create the appropriate mask if this is a control permission.
        if (this.type == TYPE_CONTROL) {
            this.mask = this.createControlMask(actions);
        }
    }

    /* ====================================================================
     * Public methods
     */

    /**
     * Returns the list of actions permitted by this instance of
     * {@code DaemonPermission} in its canonical form.
     *
     * @return The canonicalized list of actions.
     */
    @Override
    public String getActions()
    {
        if (this.type == TYPE_CONTROL) {
            return this.createControlActions(this.mask);
        }
        return "";
    }

    /**
     * Returns the hash code for this {@code DaemonPermission} instance.
     *
     * @return An hash code value.
     */
    @Override
    public int hashCode()
    {
        this.setupDescription();
        return this.desc.hashCode();
    }

    /**
     * Checks if a specified object equals {@code DaemonPermission}.
     *
     * @return <b>true</b> or <b>false</b> whether the specified object equals
     *         this {@code DaemonPermission} instance or not.
     */
    @Override
    public boolean equals(final Object object)
    {
        if (object == this) {
            return true;
        }

        if (!(object instanceof DaemonPermission)) {
            return false;
        }

        final DaemonPermission that = (DaemonPermission) object;

        if (this.type != that.type) {
            return false;
        }
        return this.mask == that.mask;
    }

    /**
     * Checks if this {@code DaemonPermission} implies another
     * {@code Permission}.
     *
     * @return <b>true</b> or <b>false</b> whether the specified permission
     *         is implied by this {@code DaemonPermission} instance or
     *         not.
     */
    @Override
    public boolean implies(final Permission permission)
    {
        if (permission == this) {
            return true;
        }

        if (!(permission instanceof DaemonPermission)) {
            return false;
        }

        final DaemonPermission that = (DaemonPermission) permission;

        if (this.type != that.type) {
            return false;
        }
        return (this.mask & that.mask) == that.mask;
    }

    /**
     * Returns a {@code String} representation of this instance.
     *
     * @return A {@code String} representing this
     *         {@code DaemonPermission} instance.
     */
    @Override
    public String toString()
    {
        this.setupDescription();
        return this.desc;
    }

    /* ====================================================================
     * Private methods
     */

    /**
     * Creates a String description for this permission instance.
     */
    private void setupDescription()
    {
        if (this.desc != null) {
            return;
        }

        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getName());
        buf.append('[');
        switch (this.type) {
            case TYPE_CONTROL:
                buf.append(CONTROL);
            break;
            default:
                buf.append("UNKNOWN");
            break;
        }
        buf.append(':');
        buf.append(this.getActions());
        buf.append(']');

        this.desc = buf.toString();
    }

    /**
     * Creates a permission mask for a given control actions string.
     */
    private int createControlMask(final String actions)
        throws IllegalArgumentException
    {
        if (actions == null) {
            return 0;
        }

        int mask = 0;
        final StringTokenizer tok = new StringTokenizer(actions, ",", false);

        while (tok.hasMoreTokens()) {
            final String val = tok.nextToken().trim();

            if (WILDCARD.equals(val)) {
                return MASK_CONTROL_START | MASK_CONTROL_STOP |
                       MASK_CONTROL_SHUTDOWN | MASK_CONTROL_RELOAD;
            }
            if (CONTROL_START.equalsIgnoreCase(val)) {
                mask = mask | MASK_CONTROL_START;
            }
            else if (CONTROL_STOP.equalsIgnoreCase(val)) {
                mask = mask | MASK_CONTROL_STOP;
            }
            else if (CONTROL_SHUTDOWN.equalsIgnoreCase(val)) {
                mask = mask | MASK_CONTROL_SHUTDOWN;
            }
            else if (CONTROL_RELOAD.equalsIgnoreCase(val)) {
                mask = mask | MASK_CONTROL_RELOAD;
            }
            else {
                throw new IllegalArgumentException("Invalid action name \"" +
                                                   val + "\" specified");
            }
        }
        return mask;
    }

    /** Creates an actions list for a given control permission mask. */
    private String createControlActions(final int mask)
    {
        final StringBuilder buf = new StringBuilder();
        boolean sep = false;

        if ((mask & MASK_CONTROL_START) == MASK_CONTROL_START) {
            sep = true;
            buf.append(CONTROL_START);
        }

        if ((mask & MASK_CONTROL_STOP) == MASK_CONTROL_STOP) {
            if (sep) {
                buf.append(",");
            }
            else {
                sep = true;
            }
            buf.append(CONTROL_STOP);
        }

        if ((mask & MASK_CONTROL_SHUTDOWN) == MASK_CONTROL_SHUTDOWN) {
            if (sep) {
                buf.append(",");
            }
            else {
                sep = true;
            }
            buf.append(CONTROL_SHUTDOWN);
        }

        if ((mask & MASK_CONTROL_RELOAD) == MASK_CONTROL_RELOAD) {
            if (sep) {
                buf.append(",");
            }
            else {
                sep = true;
            }
            buf.append(CONTROL_RELOAD);
        }

        return buf.toString();
    }
}

