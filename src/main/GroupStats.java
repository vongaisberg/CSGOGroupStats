/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * Uses the Steam Web-API to get some basic CS:GO-statistics about players in a
 * Steam group.
 * 
 * @author <a href="http://grunzwanzling.me">Maximilian von
 *         Gaisberg(Grunzwanzling)</a>
 */
public class GroupStats {

	public static void printGroupStats(String groupname) throws MalformedURLException, IOException {
		long totalKills = 0, totalDeaths = 0, totalWins = 0, totalRounds = 0, totalMoney = 0;
		String[] members;
		String group = sendHTTPRequest(
				new URL("http://steamcommunity.com/groups/" + groupname + "/memberslistxml/?xml=1"));

		group = group.substring(group.indexOf("<members>") + 10, group.indexOf("</members>") - 1);

		group = group.replaceAll("<steamID64>", "");
		group = group.replaceAll("</steamID64>", "");
		members = group.split("\n");

		String[] memberStats = new String[members.length];

		for (int i = 0; i < members.length; i++) {
			System.out.println("Checking player " + (i + 1) + "/" + members.length);
			try {
				memberStats[i] = sendHTTPRequest(
						new URL("http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key=F5FF3A717B73C8F7CC8754B0506BA53D&steamid="
								+ members[i]));
				memberStats[i] = memberStats[i].replace("	", "").replace(" ", "").replace("\"", "")
						.replace("name:", "").replace(",", "").replace("value:", "").replace("{", "");

				totalKills += getStat("total_kills", memberStats[i]);
				totalDeaths += getStat("total_deaths", memberStats[i]);
				totalWins += getStat("total_wins", memberStats[i]);
				totalRounds += getStat("total_rounds_played", memberStats[i]);
				totalMoney += getStat("total_money_earned", memberStats[i]);
			} catch (IOException e) {

			}

		}
		System.out.println("\n\nCombined stats of the members of " + groupname + "\n");
		System.out.println("Kills: " + totalKills);
		System.out.println("Deaths: " + totalDeaths);
		System.out.println("K/D: " + (double) totalKills / (double) totalDeaths);
		System.out.println("Rounds: " + totalRounds);
		System.out.println("Wins : " + totalWins);
		System.out.println("Win chance: " + (((double) totalWins / (double) totalRounds) * 100d) + "%");
		System.out.println("Total Money Earned: " + totalMoney);
	}

	/**
	 * Send HTTP requests to a webserver and fetch the answer. Will send
	 * <code>http.agent=Chrome</code> <br>
	 * <br>
	 * Taken from <a href=
	 * "https://github.com/Grunzwanzling/Essentials">github.com/Grunzwanzling/Essentials</a>
	 * 
	 * @param l
	 *            The <code>URL</code> you want to send a request to
	 * @return The answer from that <code>URL</code>
	 * @throws IOException
	 *             if connection failed
	 */
	public static String sendHTTPRequest(URL l) throws IOException {
		System.setProperty("http.agent", "Chrome");
		BufferedReader br = new BufferedReader(new InputStreamReader(l.openStream()));
		String $ = "", line = "";
		while ((line = br.readLine()) != null)
			$ += line + "\n";
		br.close();
		return $;
	}

	private static int getStat(String key, String text) {
		try {
			int startIndex = text.indexOf(key) + key.length() + 1;
			return Integer.parseInt(text.substring(startIndex, text.indexOf("}", startIndex) - 1));
		} catch (Exception e) {
			return 0;
		}

	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		printGroupStats("anonymousag");

	}
}
