package chatboot.obrabo.com.chatboot.model.request;


import com.google.gson.annotations.SerializedName;

public  class ChatBoot {

    public String question1;
    @SerializedName("Answer1")
    public String answer1;
    public String question2;
    @SerializedName("Answer2")
    public String answer2;
    public String question3;
    @SerializedName("Answer3")
    public String answer3;
    public String question4;
    @SerializedName("Answer4")
    public String answer4;
    private String image;

    public ChatBoot() {
    }

    public ChatBoot(String dateOfBirth, String fullName) {
    }

    public ChatBoot(String dateOfBirth, String fullName, String nickname) {

    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getQuestion4() {
        return question4;
    }

    public void setQuestion4(String question4) {
        this.question4 = question4;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
