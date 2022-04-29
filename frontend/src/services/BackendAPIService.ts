class BackendAPIService {
  static readonly RootURL = process.env.REACT_APP_API_URL;
  static readonly MultiPartURL =
    BackendAPIService.RootURL + "/v1/messages/multipart";

  public static async postForm(formdata: FormData): Promise<Response> {
    const requestOptions = {
      method: "POST",
      body: formdata,
    };

    console.log("Trying to post: ");
    console.log(BackendAPIService.MultiPartURL);

    return fetch(BackendAPIService.MultiPartURL, requestOptions);
  }
}

export default BackendAPIService;
