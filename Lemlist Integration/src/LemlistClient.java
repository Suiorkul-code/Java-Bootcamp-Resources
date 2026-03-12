import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import model.Campaign;
import model.Lead;

public class LemlistClient {

    private static final String BASE_URL = "https://api.lemlist.com/api";

    private final String apiKey;
    private final HttpClient httpClient;

    public LemlistClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    // -------------------------------------------------------------------------
    // Team
    // -------------------------------------------------------------------------

    public String getTeam() throws Exception {
        return get("/team");
    }

    // -------------------------------------------------------------------------
    // Campaigns
    // -------------------------------------------------------------------------

    public List<Campaign> getCampaigns() throws Exception {
        String json = get("/campaigns");
        return parseCampaigns(json);
    }

    // -------------------------------------------------------------------------
    // Leads
    // -------------------------------------------------------------------------

    public String getLeads(String campaignId) throws Exception {
        return get("/campaigns/" + encode(campaignId) + "/leads");
    }

    public String addLead(String campaignId, Lead lead) throws Exception {
        String body = buildLeadJson(lead);
        return post("/campaigns/" + encode(campaignId) + "/leads/" + encode(lead.getEmail()), body);
    }

    public String deleteLead(String campaignId, String email) throws Exception {
        return delete("/campaigns/" + encode(campaignId) + "/leads/" + encode(email));
    }

    public Campaign createCampaign(String name) throws Exception {
        String body = "{\"name\":\"" + escapeJson(name) + "\"}";
        String json = post("/campaigns", body);
        String id = extractJsonString(json.replaceFirst("^\\{", ""), "_id");
        String campaignName = extractJsonString(json.replaceFirst("^\\{", ""), "name");
        return new Campaign(id, campaignName != null ? campaignName : name);
    }

    // -------------------------------------------------------------------------
    // Activities
    // -------------------------------------------------------------------------

    public String getActivities(String type) throws Exception {
        return get("/activities?type=" + encode(type));
    }

    // -------------------------------------------------------------------------
    // HTTP helpers
    // -------------------------------------------------------------------------

    private String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(path))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return send(request);
    }

    private String post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return send(request);
    }

    private String delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(path))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        return send(request);
    }

    private String send(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new RuntimeException("API request failed with status " + statusCode + ": " + response.body());
        }
        return response.body();
    }

    private URI buildUri(String path) {
        String separator = path.contains("?") ? "&" : "?";
        return URI.create(BASE_URL + path + separator + "apiKey=" + encode(apiKey));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // -------------------------------------------------------------------------
    // JSON helpers (no external library required)
    // -------------------------------------------------------------------------

    private String buildLeadJson(Lead lead) {
        return "{"
                + "\"firstName\":\"" + escapeJson(lead.getFirstName()) + "\","
                + "\"lastName\":\"" + escapeJson(lead.getLastName()) + "\","
                + "\"companyName\":\"" + escapeJson(lead.getCompanyName()) + "\""
                + "}";
    }

    private List<Campaign> parseCampaigns(String json) {
        List<Campaign> campaigns = new ArrayList<>();
        // Each campaign object looks like: {"_id":"...","name":"..."}
        String[] entries = json.split("\\{");
        for (String entry : entries) {
            if (entry.contains("\"_id\"")) {
                String id = extractJsonString(entry, "_id");
                String name = extractJsonString(entry, "name");
                if (id != null && name != null) {
                    campaigns.add(new Campaign(id, name));
                }
            }
        }
        return campaigns;
    }

    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
