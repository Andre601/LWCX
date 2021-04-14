/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.griefcraft.integration;

import org.bukkit.entity.Player;

public interface ICurrency {

    /**
     * @return true if a currency is active
     */
    boolean isActive();

    /**
     * @return true if the Economy plugin can support the server account feature
     */
    boolean usingCentralBank();

    /**
     * Format money
     *
     * @param money The value to format
     * @return String with the formatted value
     */
    String format(double money);

    /**
     * Get the money name (e.g dollars)
     *
     * @return String representing the currency name
     */
    String getMoneyName();

    /**
     * Get the current balance for a player
     *
     * @param player The Player to get the balance from
     * @return double representing the Player's current balance
     */
    double getBalance(Player player);

    /**
     * Check the player's money to see if they can afford that
     * amount of money <b>without</b> going negative.
     *
     * @param player Player to check the balance from
     * @param money The price to check the Player's balance against
     * @return True if the Player's balance is greater or equal to the provided value
     */
    boolean canAfford(Player player, double money);

    /**
     * Check if the server account can afford the amount of money given
     *
     * @param money The price to check against the Server account's balance
     * @return True if the Server account's balance is greater or equal to the provided value
     */
    boolean canCentralBankAfford(double money);

    /**
     * Add money to a player's balance
     * If server account banking is enabled, the money is automatically withdrawn from the configured bank!
     *
     * @param player The Player to give money to
     * @param money The amount to add to the Player's balance
     * @return The Player's balance after modifying it
     */
    double addMoney(Player player, double money);

    /**
     * Remove money from a player's balance
     * If server account banking is enabled, the money is automatically added to the configured bank!
     *
     * @param player The Player to remove money from
     * @param money The amount to remove from the Player's balance
     * @return The Player's balance after modifying it
     */
    double removeMoney(Player player, double money);

}
