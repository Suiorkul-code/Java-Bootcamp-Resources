import java.util.List;

import model.Campaign;
import model.Lead;

public class Main {

    public static void main(String[] args) {
        // Set your Lemlist API key here or pass it as an environment variable:
        //   export LEMLIST_API_KEY=your_api_key_here
        String apiKey = System.getenv("LEMLIST_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("Error: LEMLIST_API_KEY environment variable is not set.");
            System.out.println("Set it with: export LEMLIST_API_KEY=your_api_key_here");
            return;
        }

        LemlistClient client = new LemlistClient(apiKey);

        // --- Get team info ---
        System.out.println("=== Team ===");
        try {
            String team = client.getTeam();
            System.out.println(team);
        } catch (Exception e) {
            System.out.println("Failed to get team: " + e.getMessage());
        }

        // --- List existing campaigns ---
        System.out.println("\n=== Existing Campaigns ===");
        try {
            List<Campaign> campaigns = client.getCampaigns();
            for (Campaign campaign : campaigns) {
                System.out.println(campaign);
            }
        } catch (Exception e) {
            System.out.println("Failed to get campaigns: " + e.getMessage());
        }

        // --- Create new campaign: Margex Competitor Outreach ---
        System.out.println("\n=== Creating Campaign: Margex Competitor Outreach ===");
        Campaign newCampaign = null;
        try {
            newCampaign = client.createCampaign("Margex Competitor Outreach");
            System.out.println("Created: " + newCampaign);
        } catch (Exception e) {
            System.out.println("Failed to create campaign: " + e.getMessage());
            return;
        }

        // --- Leads gathered from competitor research ---
        // These are editorial/review contacts at crypto media outlets and directories
        // where Margex competitors (Binance, Bybit, OKX, Bitget, MEXC) currently rank
        // but Margex is absent or under-represented.
        List<Lead> leads = List.of(
            new Lead("hello@coinbureau.com",       "",          "",          "Coin Bureau"),
            new Lead("tips@cryptoslate.com",        "",          "",          "CryptoSlate"),
            new Lead("press@coinspeaker.com",       "",          "",          "CoinSpeaker"),
            new Lead("tips@ccn.com",                "",          "",          "CCN"),
            new Lead("editor@cryptopolitan.com",    "",          "",          "Cryptopolitan"),
            new Lead("contact@cryptopolitan.com",   "",          "",          "Cryptopolitan"),
            new Lead("ops@cryptopolitan.com",       "",          "",          "Cryptopolitan"),
            new Lead("gaurav@coincodecap.com",      "Gaurav",    "",          "CoinCodeCap"),
            new Lead("harshita@coincodecap.com",    "Harshita",  "",          "CoinCodeCap"),
            new Lead("partner@tradersunion.com",    "",          "",          "TradersUnion"),
            new Lead("contact@thecryptoupdates.com","",          "",          "TheCryptoUpdates")
        );

        // --- Add each lead to the new campaign ---
        String campaignId = newCampaign.getId();
        System.out.println("\n=== Adding " + leads.size() + " Leads to Campaign: " + campaignId + " ===");
        int added = 0;
        for (Lead lead : leads) {
            try {
                String result = client.addLead(campaignId, lead);
                System.out.println("Added [" + lead.getEmail() + "]: " + result);
                added++;
            } catch (Exception e) {
                System.out.println("Failed to add [" + lead.getEmail() + "]: " + e.getMessage());
            }
        }
        System.out.println("\nSuccessfully added " + added + "/" + leads.size() + " leads.");

        // --- Verify leads in the campaign ---
        System.out.println("\n=== Leads in Campaign: " + campaignId + " ===");
        try {
            String leadsJson = client.getLeads(campaignId);
            System.out.println(leadsJson);
        } catch (Exception e) {
            System.out.println("Failed to get leads: " + e.getMessage());
        }
    }

}
