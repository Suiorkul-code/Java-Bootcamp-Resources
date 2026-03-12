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

        // --- List campaigns ---
        System.out.println("\n=== Campaigns ===");
        List<Campaign> campaigns = List.of();
        try {
            campaigns = client.getCampaigns();
            for (Campaign campaign : campaigns) {
                System.out.println(campaign);
            }
        } catch (Exception e) {
            System.out.println("Failed to get campaigns: " + e.getMessage());
        }

        // --- Add a lead to the first campaign ---
        if (!campaigns.isEmpty()) {
            String campaignId = campaigns.get(0).getId();
            Lead lead = new Lead("john.doe@example.com", "John", "Doe", "Acme Corp");
            System.out.println("\n=== Adding Lead to Campaign: " + campaignId + " ===");
            try {
                String result = client.addLead(campaignId, lead);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Failed to add lead: " + e.getMessage());
            }

            // --- Get leads in the campaign ---
            System.out.println("\n=== Leads in Campaign: " + campaignId + " ===");
            try {
                String leads = client.getLeads(campaignId);
                System.out.println(leads);
            } catch (Exception e) {
                System.out.println("Failed to get leads: " + e.getMessage());
            }
        }

        // --- Get recent activities ---
        System.out.println("\n=== Activities (emailsSent) ===");
        try {
            String activities = client.getActivities("emailsSent");
            System.out.println(activities);
        } catch (Exception e) {
            System.out.println("Failed to get activities: " + e.getMessage());
        }
    }

}
