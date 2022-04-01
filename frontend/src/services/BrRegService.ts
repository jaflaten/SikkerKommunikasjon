class BrRegService {
  static brRegURLBase = "https://data.brreg.no/enhetsregisteret/api/";

  static brRegURL = BrRegService.brRegURLBase + "enheter/";
  static brRegSubURL = BrRegService.brRegURLBase + "underenheter/";

  static brRegURLSearchName = BrRegService.brRegURLBase + "enheter?navn=";
  static brRegURLSearchSubName =
    BrRegService.brRegURLBase + "underenheter?navn=";

  public static searchAnyByNameAsync: (inputValue: string) => Promise<any> =
    async (inputValue: string) => {
      let regOrgPromis = BrRegService.searchByNameAsync(inputValue);
      let subOrgPromis = BrRegService.searchSubByNameAsync(inputValue);

      let promises = [regOrgPromis, subOrgPromis];
      return Promise.all(promises).then((res) => {
        console.log(res);
        return res.flat();
      });
    };

  private static searchByNameAsync = (inputValue: string) => {
    return fetch(BrRegService.brRegURLSearchName + inputValue)
      .then((res) => res.json())
      .then((jo) => {
        let embedded = jo["_embedded"];
        return embedded ? embedded["enheter"] : [];
      })
      .catch((e) => []);
  };

  private static searchSubByNameAsync = (inputValue: string) => {
    console.log(BrRegService.brRegURLSearchSubName + inputValue);
    return fetch(BrRegService.brRegURLSearchSubName + inputValue)
      .then((res) => res.json())
      .then((jo) => {
        let embedded = jo["_embedded"];
        return embedded ? embedded["underenheter"] : [];
      })
      .catch((e) => []);
  };

  /**
   * Combines the two fetch calls, regular org get and sub org get, returns as soon as first succesfull or both are finished.
   * @param orgNr
   * @returns
   */
  public static getAnyOrgEntityByNumAsync = async (orgNr: string) => {
    try {
      //start both fetch calls async
      let resultPromise = BrRegService.getOrgByOrgNumAsync(orgNr);
      let resultSubOrgPromise = BrRegService.getOrgSubEntityAsync(orgNr);

      //detect first fulfilled fetch
      let promises = [resultPromise, resultSubOrgPromise];
      let firstFulfilled = await Promise.any(promises);

      //if first fulfilled fetch found and org, return this
      if (firstFulfilled["organisasjonsnummer"]) return firstFulfilled;

      //if found sub org return this
      if ((await resultSubOrgPromise)["organisasjonsnummer"]) {
        return resultSubOrgPromise;
      } else {
        // return regular org, may have failed
        return resultPromise;
      }
    } catch (e) {
      console.log("Something went wrong with getAnyOrgEntityByNumAsync");
      console.log(e);
    }
  };

  private static getOrgByOrgNumAsync = async (orgNr: string) => {
    try {
      let _url = BrRegService.brRegURL + orgNr;
      return BrRegService.fetchSingleOrgFromUrl(_url);
    } catch (e) {
      console.log("Something went wrong with getOrgByOrgNumAsync");
      console.log(e);
    }
  };

  private static fetchSingleOrgFromUrl = async (_url: string) => {
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
  };

  private static getOrgSubEntityAsync = async (orgNr: string) => {
    try {
      let _url = BrRegService.brRegSubURL + orgNr;
      return BrRegService.fetchSingleOrgFromUrl(_url);
    } catch (e) {
      console.log("Something went wrong with getOrgSubEntityAsync");
      console.log(e);
    }
  };
}
export default BrRegService;
