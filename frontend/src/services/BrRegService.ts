class BrRegService {
  static brRegURLBase = "https://data.brreg.no/enhetsregisteret/api/";
  static brRegURL = BrRegService.brRegURLBase + "enheter/";

  static brRegURLSearchName = BrRegService.brRegURLBase + "enheter?navn=";

  public static searchByName = (inputValue) => {
    return fetch(BrRegService.brRegURLSearchName + inputValue)
      .then((res) => res.json())
      .then((jo) => {
        let embedded = jo["_embedded"];
        return embedded ? embedded["enheter"] : [];
      })
      .catch((e) => []);
  };

  public static getOrgByOrgNumAsync = async (orgNr) => {
    try {
      let _url = BrRegService.brRegURL + orgNr;
      let responseJson = await fetch(_url).then((res) => {
        if (res.status === 404) {
          return {
            navn: "Fant ikke organisasjonsnr",
            organisasjonsnummer: false,
          };
        } else if (res.status === 400) {
          return {
            navn: "ugyldig organisasjonsnr (9 siffer)",
            organisasjonsnummer: false,
          };
        } else {
          return res.json();
        }
      });
      return responseJson;
    } catch (e) {
      console.log("Something went wrong with fetchOrg");
      console.log(e);
    }
  };
}

export default BrRegService;
