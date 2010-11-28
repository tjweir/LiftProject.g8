package $package$ {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

import _root_.org.openid4java.discovery.DiscoveryInformation
import _root_.org.openid4java.message.AuthRequest

object MyVendor extends SimpleOpenIDVendor  { 
  def ext(di: DiscoveryInformation, authReq: AuthRequest): Unit = { 
    import WellKnownAttributes._ 
    WellKnownEndpoints.findEndpoint(di) map {ep => 
      ep.makeAttributeExtension(List(Email, FullName, FirstName, LastName)) foreach {ex =>
        authReq.addExtension(ex)
      }
    } 
  } 
  override def createAConsumer = new OpenIDConsumer[UserType] { 
    beforeAuth = Full(ext _) 
  } 
}

object User extends User with MetaOpenIDProtoUser[User] with LongKeyedMetaMapper[User] { 
  def openIDVendor = MyVendor
  override def screenWrap = Full(<lift:surround with="default" at="content"><lift:bind /></lift:surround>) 
  override def dbTableName = "users"
  override def homePage = if (loggedIn_?) "/dashboard" else "/" 
} 

class User extends OpenIDProtoUser[User] { 
  def getSingleton = User 
} 


}
}
